package com.uilover.project304.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SavedRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    private fun getFavoritesDoc(userId: String) = 
        firestore.collection("favorites").document(userId)

    fun getFavoriteIds(userId: String): Flow<List<String>> = callbackFlow {
        val listener = getFavoritesDoc(userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val data = snapshot?.data
                val propertyIds = data?.get("propertyIds") as? List<String> ?: emptyList()
                trySend(propertyIds)
            }

        awaitClose { listener.remove() }
    }

    suspend fun addToFavorites(userId: String, propertyId: String): Result<Unit> {
        return try {
            val docRef = getFavoritesDoc(userId)
            val doc = docRef.get().await()

            if (doc.exists()) {
                // Update existing document
                docRef.update("propertyIds", com.google.firebase.firestore.FieldValue.arrayUnion(propertyId)).await()
            } else {
                // Create new document
                docRef.set(mapOf("propertyIds" to listOf(propertyId))).await()
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun removeFromFavorites(userId: String, propertyId: String): Result<Unit> {
        return try {
            val docRef = getFavoritesDoc(userId)
            docRef.update("propertyIds", com.google.firebase.firestore.FieldValue.arrayRemove(propertyId)).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun isFavorite(userId: String, propertyId: String): Boolean {
        return try {
            val doc = getFavoritesDoc(userId).get().await()
            val propertyIds = doc?.data?.get("propertyIds") as? List<String> ?: emptyList()
            propertyIds.contains(propertyId)
        } catch (e: Exception) {
            false
        }
    }

    suspend fun toggleFavorite(userId: String, propertyId: String): Boolean {
        return try {
            val isFav = isFavorite(userId, propertyId)
            if (isFav) {
                removeFromFavorites(userId, propertyId)
                false
            } else {
                addToFavorites(userId, propertyId)
                true
            }
        } catch (e: Exception) {
            false
        }
    }

    suspend fun getFavoriteCount(userId: String): Int {
        return try {
            val doc = getFavoritesDoc(userId).get().await()
            val propertyIds = doc?.data?.get("propertyIds") as? List<String> ?: emptyList()
            propertyIds.size
        } catch (e: Exception) {
            0
        }
    }

    suspend fun clearFavorites(userId: String): Result<Unit> {
        return try {
            getFavoritesDoc(userId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
