package com.uilover.project304.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.uilover.project304.data.model.Property
import com.uilover.project304.data.model.PropertyCategory
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PropertyRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    private val propertiesCollection = firestore.collection("properties")

    fun getFeaturedProperties(excludedOwnerId: String? = null): Flow<List<Property>> = callbackFlow {
        val listener = propertiesCollection
            .whereEqualTo("isFeatured", true)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val properties = snapshot?.documents?.mapNotNull { doc ->
                    doc.data?.let { Property.fromMap(it) }
                }?.filter { it.isActive && it.ownerId != excludedOwnerId } ?: emptyList()

                trySend(properties)
            }

        awaitClose { listener.remove() }
    }

    fun getRecommendedProperties(excludedOwnerId: String? = null): Flow<List<Property>> = callbackFlow {
        val listener = propertiesCollection
            .orderBy("rating", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val properties = snapshot?.documents?.mapNotNull { doc ->
                    doc.data?.let { Property.fromMap(it) }
                }?.filter { it.isActive && it.ownerId != excludedOwnerId } ?: emptyList()

                trySend(properties)
            }

        awaitClose { listener.remove() }
    }

    fun getAllProperties(excludedOwnerId: String? = null): Flow<List<Property>> = callbackFlow {
        val listener = propertiesCollection
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val properties = snapshot?.documents?.mapNotNull { doc ->
                    doc.data?.let { Property.fromMap(it) }
                }?.filter { it.isActive && it.ownerId != excludedOwnerId } ?: emptyList()

                trySend(properties)
            }

        awaitClose { listener.remove() }
    }

    fun getUserProperties(userId: String): Flow<List<Property>> = callbackFlow {
        val listener = propertiesCollection
            .whereEqualTo("ownerId", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                trySend(snapshot?.documents?.mapNotNull { it.data?.let(Property::fromMap) } ?: emptyList())
            }
        awaitClose { listener.remove() }
    }

    fun getPropertiesByCategory(category: PropertyCategory): Flow<List<Property>> = callbackFlow {
        val listener = if (category == PropertyCategory.ALL) {
            propertiesCollection.addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val properties = snapshot?.documents?.mapNotNull { doc ->
                    doc.data?.let { Property.fromMap(it) }
                } ?: emptyList()
                trySend(properties.filter { it.isActive })
            }
        } else {
            propertiesCollection
                .whereEqualTo("category", category.name)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        close(error)
                        return@addSnapshotListener
                    }
                    val properties = snapshot?.documents?.mapNotNull { doc ->
                        doc.data?.let { Property.fromMap(it) }
                    } ?: emptyList()
                    trySend(properties.filter { it.isActive })
                }
        }

        awaitClose { listener.remove() }
    }

    suspend fun hasAnyProperties(): Boolean {
        return try {
            val snapshot = propertiesCollection.limit(1).get().await()
            !snapshot.isEmpty
        } catch (e: Exception) {
            // Assume properties exist on error so we never attempt to re-seed on every failed check
            true
        }
    }

    suspend fun getPropertyById(id: String): Property? {
        return try {
            val doc = propertiesCollection.document(id).get().await()
            if (doc.exists()) {
                doc.data?.let { Property.fromMap(it) }
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    suspend fun searchProperties(query: String): List<Property> {
        return try {
            val snapshot = propertiesCollection.get().await()
            val allProperties = snapshot.documents.mapNotNull { doc ->
                doc.data?.let { Property.fromMap(it) }
            }

            allProperties.filter { property ->
                property.isActive && (
                property.title.contains(query, ignoreCase = true) ||
                property.address.contains(query, ignoreCase = true) ||
                property.category.name.contains(query, ignoreCase = true)
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun addProperty(property: Property): Result<Unit> {
        return try {
            propertiesCollection.document(property.id).set(property.toMap()).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateProperty(property: Property): Result<Unit> {
        return try {
            propertiesCollection.document(property.id).update(property.toMap()).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteProperty(id: String): Result<Unit> {
        return try {
            propertiesCollection.document(id).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun setPropertyActive(id: String, isActive: Boolean): Result<Unit> {
        return try {
            propertiesCollection.document(id).update("isActive", isActive).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
