package dev.banger.hootkey.data.repository

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.Source
import dev.banger.hootkey.data.Constants.CATEGORIES
import dev.banger.hootkey.data.Constants.COMMON
import dev.banger.hootkey.data.Constants.FIELDS
import dev.banger.hootkey.data.Constants.TEMPLATES
import dev.banger.hootkey.data.Constants.VAULTS
import dev.banger.hootkey.data.crypto.CryptoManager
import dev.banger.hootkey.data.network.NetworkManager
import kotlinx.coroutines.tasks.await


fun FirebaseFirestore.vaultCollection(userId: String) =
    collection(userId).document(VAULTS).collection(VAULTS)

fun FirebaseFirestore.fieldCollection(userId: String, vaultId: String) =
    vaultCollection(userId).document(vaultId).collection(FIELDS)

fun FirebaseFirestore.templateCollection(userId: String) =
    collection(userId).document(TEMPLATES).collection(TEMPLATES)

fun FirebaseFirestore.commonTemplateCollection() =
    collection(COMMON).document(TEMPLATES).collection(TEMPLATES)

fun FirebaseFirestore.templateFieldCollection(userId: String, templateId: String) =
    templateCollection(userId).document(templateId).collection(FIELDS)

fun FirebaseFirestore.commonTemplateFieldCollection(templateId: String): CollectionReference =
    commonTemplateCollection().document(templateId).collection(FIELDS)

fun FirebaseFirestore.categoryCollection(userId: String) =
    collection(userId).document(CATEGORIES).collection(CATEGORIES)

fun FirebaseFirestore.commonCategoryCollection() =
    collection(COMMON).document(CATEGORIES).collection(CATEGORIES)

suspend fun getCategoryRefs(
    fireStore: FirebaseFirestore, templateId: String, userId: String, batchSize: Long = 100
): List<DocumentReference> {
    val result = mutableListOf<DocumentReference>()
    var read = batchSize
    var lastRef: DocumentSnapshot? = null
    while (read >= batchSize) {
        read = 0
        fireStore.categoryCollection(userId).whereEqualTo("templateId", templateId).orderBy(
            FieldPath.documentId()
        ).startAfterIfNotNull(lastRef).limit(batchSize).get().await().forEach { category ->
            result.add(category.reference)
            lastRef = category
            read++
        }
    }
    return result
}

suspend fun getVaultRefs(
    fireStore: FirebaseFirestore, categoryId: String, userId: String, batchSize: Long = 100
): List<DocumentReference> {
    val result = mutableListOf<DocumentReference>()
    var read = batchSize
    var lastRef: DocumentSnapshot? = null
    while (read >= batchSize) {
        read = 0
        fireStore.vaultCollection(userId).whereEqualTo("categoryId", categoryId)
            .orderBy(FieldPath.documentId()).startAfterIfNotNull(lastRef).limit(batchSize).get()
            .await().forEach { vault ->
                result.add(vault.reference)
                lastRef = vault
                read++
            }
    }
    return result
}

suspend fun getFieldRefs(
    vaultRefs: List<DocumentReference>, batchSize: Long = 100
): List<DocumentReference> {
    val result = mutableListOf<DocumentReference>()
    vaultRefs.forEach { vault ->
        var read = batchSize
        var lastRef: DocumentSnapshot? = null
        while (read >= batchSize) {
            read = 0
            vault.collection(FIELDS).orderBy(FieldPath.documentId()).startAfterIfNotNull(lastRef)
                .limit(batchSize).get().await().forEach { field ->
                    result.add(field.reference)
                    lastRef = field
                    read++
                }
        }
    }
    return result
}

suspend fun getFieldRefs(vault: DocumentReference, batchSize: Long = 100): List<DocumentReference> {
    val result = mutableListOf<DocumentReference>()
    var read = batchSize
    var lastRef: DocumentSnapshot? = null
    while (read >= batchSize) {
        read = 0
        vault.collection(FIELDS).orderBy(FieldPath.documentId()).startAfterIfNotNull(lastRef)
            .limit(batchSize).get().await().forEach { field ->
                result.add(field.reference)
                lastRef = field
                read++
            }
    }
    return result
}

fun Query.startAfterIfNotNull(ref: DocumentSnapshot?): Query {
    val nonNullRef: DocumentSnapshot = ref ?: return this
    return startAfter(nonNullRef)
}

inline fun String.decryptWhen(cryptoManager: CryptoManager, predicate: (String) -> Boolean) =
    if (predicate(this)) cryptoManager.decryptBase64(this) else this

suspend inline fun <T> Task<T>.awaitWhenNetworkAvailable(networkManager: NetworkManager): T? =
    if (networkManager.isNetworkAvailable) this.await() else null

fun CollectionReference.get(networkManager: NetworkManager): Task<QuerySnapshot> =
    if (networkManager.isNetworkAvailable) get() else get(Source.CACHE)

fun DocumentReference.get(networkManager: NetworkManager): Task<DocumentSnapshot> =
    if (networkManager.isNetworkAvailable) get() else get(Source.CACHE)