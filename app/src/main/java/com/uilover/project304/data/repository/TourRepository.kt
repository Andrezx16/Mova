package com.uilover.project304.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.uilover.project304.data.model.TourBooking
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TourRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    private val toursCollection = firestore.collection("tours")

    fun getUserTours(userId: String): Flow<List<TourBooking>> = callbackFlow {
        val listener = toursCollection
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val tours = snapshot?.documents?.mapNotNull { doc ->
                    doc.data?.let { TourBooking.fromMap(it) }
                }?.sortedByDescending { it.createdAt } ?: emptyList()

                trySend(tours)
            }

        awaitClose { listener.remove() }
    }

    fun getOwnerTours(ownerId: String): Flow<List<TourBooking>> = callbackFlow {
        val listener = toursCollection
            .whereEqualTo("propertyOwnerId", ownerId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val tours = snapshot?.documents
                    ?.mapNotNull { it.data?.let(TourBooking::fromMap) }
                    ?.sortedByDescending { it.createdAt }
                    ?: emptyList()
                trySend(tours)
            }
        awaitClose { listener.remove() }
    }

    suspend fun createTourBooking(tour: TourBooking): Result<TourBooking> {
        return try {
            val docRef = toursCollection.document()
            val tourWithId = tour.copy(id = docRef.id)
            docRef.set(tourWithId.toMap()).await()
            Result.success(tourWithId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun cancelTour(tourId: String): Result<Unit> {
        return updateTourStatus(tourId, TourBooking.STATUS_CANCELLED)
    }

    suspend fun completeTour(tourId: String): Result<Unit> {
        return updateTourStatus(tourId, TourBooking.STATUS_COMPLETED)
    }

    suspend fun respondToTour(tourId: String, accepted: Boolean): Result<Unit> {
        return updateTourStatus(
            tourId,
            if (accepted) TourBooking.STATUS_CONFIRMED else TourBooking.STATUS_REJECTED
        )
    }

    suspend fun deleteTour(tourId: String): Result<Unit> {
        return try {
            toursCollection.document(tourId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteTours(tourIds: List<String>): Result<Unit> {
        return try {
            if (tourIds.isNotEmpty()) {
                val batch = firestore.batch()
                tourIds.forEach { batch.delete(toursCollection.document(it)) }
                batch.commit().await()
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun updateTourStatus(tourId: String, status: String): Result<Unit> {
        return try {
            toursCollection.document(tourId)
                .update("status", status)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getTourById(tourId: String): TourBooking? {
        return try {
            val doc = toursCollection.document(tourId).get().await()
            if (doc.exists()) {
                doc.data?.let { TourBooking.fromMap(it) }
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getPropertyTours(propertyId: String): List<TourBooking> {
        return try {
            val snapshot = toursCollection
                .whereEqualTo("propertyId", propertyId)
                .get()
                .await()

            snapshot.documents.mapNotNull { doc ->
                doc.data?.let { TourBooking.fromMap(it) }
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getUserTourCount(userId: String): Int {
        return try {
            val snapshot = toursCollection
                .whereEqualTo("userId", userId)
                .get()
                .await()
            snapshot.documents.count { document ->
                (document.getString("status") ?: TourBooking.STATUS_PENDING) in setOf(
                    TourBooking.STATUS_PENDING,
                    TourBooking.STATUS_CONFIRMED
                )
            }
        } catch (e: Exception) {
            0
        }
    }
}
