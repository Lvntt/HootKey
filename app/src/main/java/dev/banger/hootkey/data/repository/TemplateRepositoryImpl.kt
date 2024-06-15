package dev.banger.hootkey.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import dev.banger.hootkey.data.Constants.COMMON
import dev.banger.hootkey.data.Constants.EMPTY_STRING
import dev.banger.hootkey.data.crypto.CryptoManager
import dev.banger.hootkey.data.model.FieldModel
import dev.banger.hootkey.data.model.TemplateModel
import dev.banger.hootkey.domain.entity.auth.exception.UnauthorizedException
import dev.banger.hootkey.domain.entity.template.CreateTemplateRequest
import dev.banger.hootkey.domain.entity.template.FieldType
import dev.banger.hootkey.domain.entity.template.Template
import dev.banger.hootkey.domain.entity.template.TemplateCreationException
import dev.banger.hootkey.domain.entity.template.TemplateField
import dev.banger.hootkey.domain.entity.template.TemplateShort
import dev.banger.hootkey.domain.repository.TemplateRepository
import kotlinx.coroutines.tasks.await

class TemplateRepositoryImpl(
    private val fireStore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val crypto: CryptoManager
) : TemplateRepository {

    private companion object {
        const val TEMPLATES = "templates"
        const val FIELDS = "fields"
    }

    private fun templateCollection(userId: String) =
        fireStore.collection(userId).document(TEMPLATES).collection(TEMPLATES)

    private fun commonTemplateCollection() =
        fireStore.collection(COMMON).document(TEMPLATES).collection(TEMPLATES)

    private fun templateFieldCollection(userId: String, templateId: String) =
        templateCollection(userId).document(templateId).collection(FIELDS)

    private fun commonTemplateFieldCollection(templateId: String) =
        commonTemplateCollection().document(templateId).collection(FIELDS)

    private suspend inline fun CollectionReference.getTemplates(
        customCollection: Boolean, getTemplateFieldsById: (String) -> CollectionReference
    ): List<Template> = this.get().await().map { template ->
        template.toTemplate(isCustom = customCollection, getTemplateFields = {
            getTemplateFieldsById(template.id)
        })
    }

    private suspend inline fun DocumentSnapshot.toTemplate(
        isCustom: Boolean, getTemplateFields: () -> CollectionReference
    ): Template = Template(
        id = id,
        name = toObject<TemplateModel>()?.name?.decryptIfCustom(isCustom) ?: EMPTY_STRING,
        fields = getTemplateFields().get().await().map { field ->
            val fieldObject = field.toObject<FieldModel>()
            TemplateField(
                index = field.id.toInt(),
                name = fieldObject.name.decryptIfCustom(isCustom),
                type = FieldType.entries[fieldObject.type]
            )
        },
        isCustom = isCustom
    )

    private suspend inline fun CollectionReference.getShortTemplates(
        customCollection: Boolean
    ): List<TemplateShort> = this.get().await().map { template ->
        template.toTemplateShort(isCustom = customCollection)
    }

    private fun DocumentSnapshot.toTemplateShort(isCustom: Boolean): TemplateShort = TemplateShort(
        id = id,
        name = toObject<TemplateModel>()?.name?.decryptIfCustom(isCustom) ?: EMPTY_STRING,
        isCustom = isCustom
    )

    override suspend fun templateExists(id: String): Boolean {
        val userId = auth.currentUser?.uid ?: throw UnauthorizedException()
        return templateCollection(userId).document(id).get().await()
            .exists() || commonTemplateCollection().document(id).get().await().exists()
    }

    override suspend fun getById(id: String): Template? {
        val userId = auth.currentUser?.uid ?: throw UnauthorizedException()

        val customTemplate = templateCollection(userId).document(id).get().await()
        if (customTemplate.exists()) return customTemplate.toTemplate(
            isCustom = true,
            getTemplateFields = {
                templateFieldCollection(userId, id)
            })
        val commonTemplate = commonTemplateCollection().document(id).get().await()
        if (commonTemplate.exists()) return commonTemplate.toTemplate(
            isCustom = false,
            getTemplateFields = {
                commonTemplateFieldCollection(id)
            })
        return null
    }

    override suspend fun getAllFull(): List<Template> {
        val userId = auth.currentUser?.uid ?: throw UnauthorizedException()

        val userTemplates = templateCollection(userId).getTemplates(customCollection = true,
            getTemplateFieldsById = { templateId -> templateFieldCollection(userId, templateId) })
        val commonTemplates = commonTemplateCollection().getTemplates(customCollection = false,
            getTemplateFieldsById = { templateId -> commonTemplateFieldCollection(templateId) })

        return userTemplates + commonTemplates
    }

    override suspend fun getAllShort(): List<TemplateShort> {
        val userId = auth.currentUser?.uid ?: throw UnauthorizedException()

        return templateCollection(userId).getShortTemplates(customCollection = true) + commonTemplateCollection().getShortTemplates(
            customCollection = false
        )
    }

    override suspend fun create(template: CreateTemplateRequest): Template {
        val userId = auth.currentUser?.uid ?: throw UnauthorizedException()

        var templateId = EMPTY_STRING
        return runCatching {
            val templateModel = TemplateModel(
                crypto.encryptBase64(template.name)
            )
            templateId = templateCollection(userId).add(templateModel).await().id

            val fieldCollection = templateFieldCollection(userId, templateId)
            template.fields.forEach { field ->
                fieldCollection.document("${field.index}").set(
                    FieldModel(
                        name = crypto.encryptBase64(field.name), type = field.type.ordinal
                    )
                ).await()
            }

            Template(
                id = templateId, name = template.name, isCustom = true, fields = template.fields
            )
        }.onFailure {
            //Try to roll back the changes before throwing an exception
            if (templateId.isNotBlank()) templateCollection(userId).document(templateId).delete()
                .await()
            throw TemplateCreationException("Failed to create template : ${it.stackTraceToString()}")
        }.getOrThrow()
    }

    override suspend fun delete(id: String) {
        val userId = auth.currentUser?.uid ?: throw UnauthorizedException()

        val categoryRefs = getCategoryRefs(fireStore, id, userId)
        val vaultRefs =
            categoryRefs.flatMap { categoryRef -> getVaultRefs(fireStore, categoryRef.id, userId) }
        val fieldRefs = getFieldRefs(vaultRefs)

        //TODO add specific exception when internet is unavailable
        fireStore.runTransaction { transaction ->
            transaction.delete(templateCollection(userId).document(id))
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

    private fun String.decryptIfCustom(isCustom: Boolean) =
        if (isCustom) crypto.decryptBase64(this) else this

}