package com.ehealth.dermassist.data.repository

import com.ehealth.dermassist.domain.model.User
import com.ehealth.dermassist.domain.repository.AppRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class AppRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : AppRepository {

    override fun getUser(): Flow<User?> = callbackFlow {
        val listener =
            FirebaseAuth.AuthStateListener { auth ->
                val firebaseUser = auth.currentUser
                val user =
                    firebaseUser?.let {
                        User(
                            id = it.uid,
                            email = it.email ?: "",
                            name = it.displayName ?: "",
                            age = 23,
                            skinType = "Redness",
                            memberSince = "23 Aug 2023",
                        )
                    }
                trySend(user)
            }
        firebaseAuth.addAuthStateListener(listener)
        awaitClose { firebaseAuth.removeAuthStateListener(listener) }
    }

    override suspend fun signInWithGoogle(idToken: String): Result<Unit> {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            firebaseAuth.signInWithCredential(credential).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun loginWithEmail(email: String, pass: String): Result<Unit> {
        return try {
            firebaseAuth.signInWithEmailAndPassword(email, pass).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signUpWithEmail(email: String, pass: String, name: String): Result<Unit> {
        return try {
            val result = firebaseAuth.createUserWithEmailAndPassword(email, pass).await()
            val user = result.user
            val profileUpdates = UserProfileChangeRequest.Builder().setDisplayName(name).build()
            user?.updateProfile(profileUpdates)?.await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun logout() {
        firebaseAuth.signOut()
    }

    override suspend fun clearUserData(): Result<Unit> {
        return try {
            val user = firebaseAuth.currentUser
            if (user != null) {
                val userId = user.uid
                // 1. Delete user document from Firestore
                //TODO
              //  firestore.collection("users").document(userId).delete().await()
                
                // 2. Delete user from Firebase Auth
                // Note: This may require a recent login for security reasons.
                user.delete().await()
                
                Result.success(Unit)
            } else {
                Result.failure(Exception("User not logged in"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
