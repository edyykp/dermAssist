package com.ehealth.dermassist.domain.repository

import com.ehealth.dermassist.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AppRepository {
    fun getUser(): Flow<com.ehealth.dermassist.domain.model.User?>

    suspend fun login(email: String, pass: String): Result<Unit>

    suspend fun logout()
}
