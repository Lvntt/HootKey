package dev.banger.hootkey.data.repository

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import dev.banger.hootkey.data.Constants.CATEGORIES
import dev.banger.hootkey.data.Constants.FIELDS
import dev.banger.hootkey.data.Constants.VAULTS
import kotlinx.coroutines.tasks.await

suspend fun getCategoryRefs(
    fireStore: FirebaseFirestore, templateId: String, userId: String, batchSize: Long = 100
): List<DocumentReference> {
    val result = mutableListOf<DocumentReference>()
    var read = batchSize
    var lastRef: DocumentSnapshot? = null
    while (read >= batchSize) {
        read = 0
        fireStore.collection(userId).document(CATEGORIES).collection(CATEGORIES)
            .whereEqualTo("templateId", templateId).orderBy(
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
        fireStore.collection(userId).document(VAULTS).collection(
            VAULTS
        ).whereEqualTo("categoryId", categoryId).orderBy(FieldPath.documentId())
            .startAfterIfNotNull(lastRef).limit(batchSize).get().await().forEach { vault ->
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