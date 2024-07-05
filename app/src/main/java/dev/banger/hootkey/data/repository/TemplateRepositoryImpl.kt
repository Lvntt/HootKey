package dev.banger.hootkey.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import dev.banger.hootkey.data.Constants.EMPTY_STRING
import dev.banger.hootkey.data.crypto.CryptoManager
import dev.banger.hootkey.data.model.FieldModel
import dev.banger.hootkey.data.model.TemplateModel
import dev.banger.hootkey.data.network.NetworkManager
import dev.banger.hootkey.domain.entity.OfflineException
import dev.banger.hootkey.domain.entity.auth.exception.UnauthorizedException
import dev.banger.hootkey.domain.entity.template.CreateTemplateRequest
import dev.banger.hootkey.domain.entity.template.FieldType
import dev.banger.hootkey.domain.entity.template.Template
import dev.banger.hootkey.domain.entity.template.TemplateCreationException
import dev.banger.hootkey.domain.entity.template.TemplateField
import dev.banger.hootkey.domain.entity.template.TemplateShort
import dev.banger.hootkey.domain.repository.TemplateRepository
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TemplateRepositoryImpl @Inject constructor(
    private val fireStore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val crypto: CryptoManager,
    private val network: NetworkManager
) : TemplateRepository {

    private suspend inline fun CollectionReference.getTemplates(
        customCollection: Boolean, getTemplateFieldsById: (String) -> CollectionReference
    ): List<Template> = this.get(network).await().map { template ->
        template.toTemplate(isCustom = customCollection, getTemplateFields = {
            getTemplateFieldsById(template.id)
        })
    }

    private suspend inline fun DocumentSnapshot.toTemplate(
        isCustom: Boolean, getTemplateFields: () -> CollectionReference
    ): Template = Template(id = id,
        name = toObject<TemplateModel>()?.name?.decryptWhen(crypto) { isCustom } ?: EMPTY_STRING,
        fields = getTemplateFields().get(network).await().map { field ->
            val fieldObject = field.toObject<FieldModel>()
            TemplateField(
                index = field.id.toInt(),
                name = fieldObject.name.decryptWhen(crypto) { isCustom },
                type = FieldType.entries[fieldObject.type]
            )
        },
        isCustom = isCustom
    )

    private suspend inline fun CollectionReference.getShortTemplates(
        customCollection: Boolean
    ): List<TemplateShort> = this.get(network).await().map { template ->
        template.toTemplateShort(isCustom = customCollection)
    }

    private fun DocumentSnapshot.toTemplateShort(isCustom: Boolean): TemplateShort = TemplateShort(
        id = id,
        name = toObject<TemplateModel>()?.name?.decryptWhen(crypto) { isCustom } ?: EMPTY_STRING,
        isCustom = isCustom)

    override suspend fun templateExists(id: String): Boolean {
        val userId = auth.currentUser?.uid ?: throw UnauthorizedException()
        return fireStore.templateCollection(userId).document(id).get(network).await()
            .exists() || fireStore.commonTemplateCollection().document(id).get(network).await().exists()
    }

    override suspend fun getById(id: String): Template? {
        val userId = auth.currentUser?.uid ?: throw UnauthorizedException()

        val customTemplate = fireStore.templateCollection(userId).document(id).get(network).await()
        if (customTemplate.exists()) return customTemplate.toTemplate(isCustom = true,
            getTemplateFields = { fireStore.templateFieldCollection(userId, id) })

        val commonTemplate = fireStore.commonTemplateCollection().document(id).get(network).await()
        if (commonTemplate.exists()) return commonTemplate.toTemplate(isCustom = false,
            getTemplateFields = { fireStore.commonTemplateFieldCollection(id) })

        return null
    }

    override suspend fun getAllFull(): List<Template> {
        val userId = auth.currentUser?.uid ?: throw UnauthorizedException()

        val userTemplates = fireStore.templateCollection(userId).getTemplates(customCollection = true,
            getTemplateFieldsById = { templateId -> fireStore.templateFieldCollection(userId, templateId) })
        val commonTemplates = fireStore.commonTemplateCollection().getTemplates(customCollection = false,
            getTemplateFieldsById = { templateId -> fireStore.commonTemplateFieldCollection(templateId) })

        return userTemplates + commonTemplates
    }

    override suspend fun getAllShort(): List<TemplateShort> {
        val userId = auth.currentUser?.uid ?: throw UnauthorizedException()

        return fireStore.templateCollection(userId).getShortTemplates(customCollection = true) +
                fireStore.commonTemplateCollection().getShortTemplates(customCollection = false)
    }

    override suspend fun create(template: CreateTemplateRequest): Template {
        val userId = auth.currentUser?.uid ?: throw UnauthorizedException()

        val templateId = UUID.randomUUID().toString()
        return runCatching {
            val templateModel = TemplateModel(
                crypto.encryptBase64(template.name)
            )
            fireStore.templateCollection(userId).document(templateId).set(templateModel)
                .awaitWhenNetworkAvailable(network)

            val fieldCollection = fireStore.templateFieldCollection(userId, templateId)
            template.fields.forEach { field ->
                fieldCollection.document("${field.index}").set(
                    FieldModel(
                        name = crypto.encryptBase64(field.name), type = field.type.ordinal
                    )
                ).awaitWhenNetworkAvailable(network)
            }

            Template(
                id = templateId, name = template.name, isCustom = true, fields = template.fields
            )
        }.onFailure {
            //Try to roll back the changes before throwing an exception
            fireStore.templateCollection(userId).document(templateId).delete()
                .awaitWhenNetworkAvailable(network)
            throw TemplateCreationException("Failed to create template : ${it.stackTraceToString()}")
        }.getOrThrow()
    }

    override suspend fun delete(id: String) {
        if (!network.isNetworkAvailable)
            throw OfflineException("Deleting a template is impossible without network as it requires a transaction")
        val userId = auth.currentUser?.uid ?: throw UnauthorizedException()

        val categoryRefs = getCategoryRefs(fireStore, id, userId)
        val vaultRefs =
            categoryRefs.flatMap { categoryRef -> getVaultRefs(fireStore, categoryRef.id, userId) }
        val fieldRefs = getFieldRefs(vaultRefs)

        fireStore.runTransaction { transaction ->
            transaction.delete(fireStore.templateCollection(userId).document(id))
            categoryRefs.forEach { categoryRef ->
                transaction.delete(categoryRef)
            }
            vaultRefs.forEach { vaultRef ->
                transaction.delete(vaultRef)
            }
            fieldRefs.forEach { fieldRef ->
                transaction.delete(fieldRef)
            }
        }.await()
    }

}