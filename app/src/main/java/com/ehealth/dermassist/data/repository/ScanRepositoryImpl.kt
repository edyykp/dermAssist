package com.ehealth.dermassist.data.repository

import com.ehealth.dermassist.domain.model.ScanHistoryItem
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

    // ─────────────────────────────
    // ALL SCANS (REAL-TIME)
    // ─────────────────────────────
    override fun getUserScans(userId: String): Flow<List<ScanHistoryItem>> = callbackFlow {
        val listener =
            collection
                .whereEqualTo("userId", userId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .addSnapshotListener { snapshot, error ->
                    if (error != null || snapshot == null) {
                        trySend(emptyList())
                        return@addSnapshotListener
                    }

                    val scans =
                        snapshot.documents.mapNotNull { doc ->
                            doc.toObject(ScanHistoryItem::class.java)?.copy(id = doc.id)
                        }

                    trySend(scans)
                }

        awaitClose { listener.remove() }
    }

    // ─────────────────────────────
    // SINGLE SCAN DETAILS
    // ─────────────────────────────
    override suspend fun getScanDetails(userId: String, scanId: String): ScanHistoryItem? {
        return try {
            val doc = collection.document(scanId).get().await()

            if (doc.exists()) {
                doc.toObject(ScanHistoryItem::class.java)?.copy(id = doc.id)
            } else null
        } catch (e: Exception) {
            null
        }
    }

    // ─────────────────────────────
    // TOTAL SCANS COUNT
    // ─────────────────────────────
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

    // ─────────────────────────────
    // LATEST SCAN
    // ─────────────────────────────
    override suspend fun getLatestScan(userId: String): ScanHistoryItem? {
        return try {
            val snapshot =
                collection
                    .whereEqualTo("userId", userId)
                    .orderBy("createdAt", Query.Direction.DESCENDING)
                    .limit(1)
                    .get()
                    .await()

            snapshot.documents.firstOrNull()?.let { doc ->
                doc.toObject(ScanHistoryItem::class.java)?.copy(id = doc.id)
            }
        } catch (e: Exception) {
            null
        }
    }

    // ─────────────────────────────
    // ADD NEW SCAN
    // ─────────────────────────────
    override suspend fun addScan(scan: ScanHistoryItem): Result<Unit> {
        return try {
            val docRef = collection.document()

            val data = scan.copy(id = docRef.id)

            docRef.set(data).await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
