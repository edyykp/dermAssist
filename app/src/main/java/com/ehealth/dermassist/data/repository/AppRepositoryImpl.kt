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

class AppRepositoryImpl
@Inject
constructor(private val firebaseAuth: FirebaseAuth, private val firestore: FirebaseFirestore) :
    AppRepository {

    override fun getUser(): Flow<User?> = callbackFlow {
        val listener =
            FirebaseAuth.AuthStateListener { auth ->
                val firebaseUser = auth.currentUser

                if (firebaseUser == null) {
                    trySend(null)
                    return@AuthStateListener
                }

                val userId = firebaseUser.uid

                // 🔥 Listen to Firestore user document
                val registration =
                    firestore.collection("users").document(userId).addSnapshotListener { snapshot, _
                        ->
                        if (snapshot != null && snapshot.exists()) {
                            val age = snapshot.getLong("age")?.toInt()
                            val memberSinceMillis = snapshot.getLong("memberSince")
                            val name = snapshot.getString("name") ?: firebaseUser.displayName ?: ""

                            val memberSince =
                                memberSinceMillis?.let {
                                    val format =
                                        java.text.SimpleDateFormat("MMM yyyy", java.util.Locale.US)
                                    format.format(java.util.Date(it))
                                } ?: ""

                            val user =
                                User(
                                    id = userId,
                                    email = firebaseUser.email ?: "",
                                    name = name,
                                    age = age,
                                    memberSince = memberSince,
                                )

                            trySend(user)
                        }
                    }
            }

        firebaseAuth.addAuthStateListener(listener)

        awaitClose { firebaseAuth.removeAuthStateListener(listener) }
    }

    override suspend fun signInWithGoogle(idToken: String): Result<Unit> {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val result = firebaseAuth.signInWithCredential(credential).await()

            val user = result.user ?: throw Exception("User null")

            val userRef = firestore.collection("users").document(user.uid)
            val snapshot = userRef.get().await()

            // 🔥 If user does NOT exist → create it
            if (!snapshot.exists()) {
                val userData =
                    mapOf(
                        "email" to (user.email ?: ""),
                        "name" to (user.displayName ?: ""),
                        "age" to null,
                        "memberSince" to System.currentTimeMillis(),
                    )

                userRef.set(userData).await()
            }

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

            val user = result.user ?: throw Exception("User null")

            // Update display name
            val profileUpdates = UserProfileChangeRequest.Builder().setDisplayName(name).build()

            user.updateProfile(profileUpdates).await()

            // 🔥 Save extra data in Firestore
            val userData =
                mapOf(
                    "email" to email,
                    "name" to name,
                    "age" to null,
                    "memberSince" to System.currentTimeMillis(),
                )

            firestore.collection("users").document(user.uid).set(userData).await()

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

                firestore.collection("users").document(userId).delete().await()
                user.delete().await()

                Result.success(Unit)
            } else {
                Result.failure(Exception("User not logged in"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateUser(name: String, age: Int): Result<Unit> {
        return try {
            val userId = firebaseAuth.currentUser?.uid ?: throw Exception("User not logged in")

            firestore.collection("users").document(userId).update("name", name, "age", age).await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
