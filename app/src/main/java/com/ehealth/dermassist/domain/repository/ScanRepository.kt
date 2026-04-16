package com.ehealth.dermassist.domain.repository

import com.ehealth.dermassist.data.model.ScanEntity
import kotlinx.coroutines.flow.Flow

interface ScanRepository {

    fun getUserScans(userId: String): Flow<List<ScanEntity>>

    suspend fun getScanDetails(userId: String, scanId: String): ScanEntity?

    fun getTotalScans(userId: String): Flow<Int>

    suspend fun getLatestScan(userId: String): ScanEntity?

    suspend fun addScan(scan: ScanEntity): Result<Unit>
}
