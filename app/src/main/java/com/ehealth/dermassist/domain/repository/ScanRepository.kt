package com.ehealth.dermassist.domain.repository

import com.ehealth.dermassist.domain.model.ScanHistoryItem
import kotlinx.coroutines.flow.Flow

interface ScanRepository {

    fun getUserScans(userId: String): Flow<List<ScanHistoryItem>>

    suspend fun getScanDetails(userId: String, scanId: String): ScanHistoryItem?

    fun getTotalScans(userId: String): Flow<Int>

    suspend fun getLatestScan(userId: String): ScanHistoryItem?

    suspend fun addScan(scan: ScanHistoryItem): Result<Unit>
}
