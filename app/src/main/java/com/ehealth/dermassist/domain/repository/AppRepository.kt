package com.ehealth.dermassist.domain.repository

import com.ehealth.dermassist.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AppRepository {
    fun getUser(): Flow<User?>

    suspend fun signInWithGoogle(idToken: String): Result<Unit>

    suspend fun loginWithEmail(email: String, pass: String): Result<Unit>

    suspend fun signUpWithEmail(email: String, pass: String, name: String): Result<Unit>

    suspend fun logout()
}
