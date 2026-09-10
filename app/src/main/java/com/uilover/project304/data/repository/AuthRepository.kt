package com.uilover.project304.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.uilover.project304.data.model.UserProfile
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {
    val currentUser: FirebaseUser? get() = auth.currentUser
    val isLoggedIn: Boolean get() = auth.currentUser != null

    suspend fun signUp(email: String, password: String, name: String): Result<UserProfile> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val user = result.user

            if (user != null) {
                // Create user profile in Firestore
                val userProfile = UserProfile(
                    uid = user.uid,
                    name = name,
                    email = email,
                    phone = "",
                    membershipStatus = "Standard",
                    avatarUrl = "https://ui-avatars.com/api/?name=${name.replace(" ", "+")}&background=1A237E&color=fff",
                    bio = ""
                )

                firestore.collection("users")
                    .document(user.uid)
                    .set(userProfile.toMap())
                    .await()

                Result.success(userProfile)
            } else {
                Result.failure(Exception("User creation failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signIn(email: String, password: String): Result<UserProfile> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val user = result.user

            if (user != null) {
                val userProfile = getUserProfile(user.uid)
                if (userProfile != null) {
                    Result.success(userProfile)
                } else {
                    // Create profile if doesn't exist
                    val newProfile = UserProfile(
                        uid = user.uid,
                        name = user.displayName ?: "",
                        email = user.email ?: "",
                        avatarUrl = user.photoUrl?.toString() ?: "https://ui-avatars.com/api/?name=${user.displayName?.replace(" ", "+") ?: user.email}&background=1A237E&color=fff"
                    )
                    firestore.collection("users")
                        .document(user.uid)
                        .set(newProfile.toMap())
                        .await()
                    Result.success(newProfile)
                }
            } else {
                Result.failure(Exception("Sign in failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUserProfile(uid: String): UserProfile? {
        return try {
            val doc = firestore.collection("users").document(uid).get().await()
            if (doc.exists()) {
                UserProfile.fromMap(doc.data ?: emptyMap())
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    suspend fun updateUserProfile(profile: UserProfile): Result<Unit> {
        return try {
            firestore.collection("users")
                .document(profile.uid)
                .update(profile.toMap())
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun signOut() {
        auth.signOut()
    }

    suspend fun deleteUser(): Result<Unit> {
        return try {
            currentUser?.delete()?.await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
