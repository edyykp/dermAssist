package com.ehealth.dermassist.data.repository

import android.util.Log
import com.ehealth.dermassist.data.model.ScanEntity
import com.ehealth.dermassist.domain.repository.ScanRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import javax.inject.Inject
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class ScanRepositoryImpl @Inject constructor(private val firestore: FirebaseFirestore) :
    ScanRepository {

    private val TAG = "ScanRepository"

    // Helper to get user-specific scans collection
    private fun getUserScansCollection(userId: String) =
        firestore.collection("users").document(userId).collection("scans")

    override fun getUserScans(userId: String): Flow<List<ScanEntity>> = callbackFlow {
        if (userId.isBlank()) {
            trySend(emptyList())
            return@callbackFlow
        }

        val listener =
            getUserScansCollection(userId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.e(TAG, "Error fetching user scans for $userId: ${error.message}", error)
                        trySend(emptyList())
                        return@addSnapshotListener
                    }

                    if (snapshot == null) {
                        trySend(emptyList())
                        return@addSnapshotListener
                    }

                    Log.d(TAG, "Fetched ${snapshot.size()} documents for user $userId")

                    val items =
                        snapshot.documents.mapNotNull { doc ->
                            try {
                                doc.toObject(ScanEntity::class.java)?.copy(id = doc.id)
                            } catch (e: Exception) {
                                Log.e(TAG, "Error mapping document ${doc.id} to ScanEntity", e)
                                null
                            }
                        }

                    trySend(items)
                }

        awaitClose { listener.remove() }
    }

    override suspend fun getScanDetails(userId: String, scanId: String): ScanEntity? {
        return try {
            val doc = getUserScansCollection(userId).document(scanId).get().await()
            if (doc.exists()) {
                doc.toObject(ScanEntity::class.java)?.copy(id = doc.id)
            } else null
        } catch (e: Exception) {
            Log.e(TAG, "Error getting scan details: ${e.message}")
            null
        }
    }

    override fun getTotalScans(userId: String): Flow<Int> = callbackFlow {
        if (userId.isBlank()) {
            trySend(0)
            return@callbackFlow
        }

        val listener =
            getUserScansCollection(userId).addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) {
                    trySend(0)
                    return@addSnapshotListener
                }
                trySend(snapshot.size())
            }
        awaitClose { listener.remove() }
    }

    override suspend fun getLatestScan(userId: String): ScanEntity? {
        if (userId.isBlank()) return null
        return try {
            val snapshot =
                getUserScansCollection(userId)
                    .orderBy("createdAt", Query.Direction.DESCENDING)
                    .limit(1)
                    .get()
                    .await()

            snapshot.documents.firstOrNull()?.let { doc ->
                doc.toObject(ScanEntity::class.java)?.copy(id = doc.id)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting latest scan: ${e.message}")
            null
        }
    }

    override suspend fun addScan(scan: ScanEntity): Result<Unit> {
        return try {
            val docRef = getUserScansCollection(scan.userId).document()
            val data = scan.copy(id = docRef.id, createdAt = System.currentTimeMillis())
            docRef.set(data).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error adding scan: ${e.message}")
            Result.failure(e)
        }
    }
}
