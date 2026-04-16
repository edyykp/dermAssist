package com.ehealth.dermassist.data.repository

import com.ehealth.dermassist.data.model.ScanEntity
import com.ehealth.dermassist.domain.repository.ScanRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import javax.inject.Inject
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class ScanRepositoryImpl @Inject constructor(firestore: FirebaseFirestore) : ScanRepository {

    private val collection = firestore.collection("scans")

    override fun getUserScans(userId: String): Flow<List<ScanEntity>> = callbackFlow {
        val listener =
            collection
                .whereEqualTo("userId", userId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .addSnapshotListener { snapshot, error ->
                    if (error != null || snapshot == null) {
                        trySend(emptyList())
                        return@addSnapshotListener
                    }

                    val items =
                        snapshot.documents.mapNotNull { doc ->
                            doc.toObject(ScanEntity::class.java)?.copy(id = doc.id)
                        }

                    trySend(items)
                }

        awaitClose { listener.remove() }
    }

    override suspend fun getScanDetails(userId: String, scanId: String): ScanEntity? {
        return try {
            val doc = collection.document(scanId).get().await()
            if (doc.exists()) {
                doc.toObject(ScanEntity::class.java)?.copy(id = doc.id)
            } else null
        } catch (_: Exception) {
            null
        }
    }

    override fun getTotalScans(userId: String): Flow<Int> = callbackFlow {
        val listener =
            collection.whereEqualTo("userId", userId).addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) {
                    trySend(0)
                    return@addSnapshotListener
                }
                trySend(snapshot.size())
            }
        awaitClose { listener.remove() }
    }

    override suspend fun getLatestScan(userId: String): ScanEntity? {
        return try {
            val snapshot =
                collection
                    .whereEqualTo("userId", userId)
                    .orderBy("createdAt", Query.Direction.DESCENDING)
                    .limit(1)
                    .get()
                    .await()

            snapshot.documents.firstOrNull()?.let { doc ->
                doc.toObject(ScanEntity::class.java)?.copy(id = doc.id)
            }
        } catch (_: Exception) {
            null
        }
    }

    override suspend fun addScan(scan: ScanEntity): Result<Unit> {
        return try {
            val docRef = collection.document()
            val data = scan.copy(id = docRef.id, createdAt = System.currentTimeMillis())
            docRef.set(data).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
