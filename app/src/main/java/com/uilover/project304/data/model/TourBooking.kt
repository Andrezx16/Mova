package com.uilover.project304.data.model

import com.google.firebase.Timestamp

data class TourBooking(
    val id: String = "",
    val userId: String = "",
    val propertyId: String = "",
    val propertyOwnerId: String = "",
    val propertyTitle: String = "",
    val date: String = "",
    val time: String = "",
    val tourType: String = "",
    val status: String = STATUS_PENDING,
    val createdAt: Timestamp? = null
) {
    fun toMap(): Map<String, Any> {
        return mapOf(
            "id" to id,
            "userId" to userId,
            "propertyId" to propertyId,
            "propertyOwnerId" to propertyOwnerId,
            "propertyTitle" to propertyTitle,
            "date" to date,
            "time" to time,
            "tourType" to tourType,
            "status" to status,
            "createdAt" to (createdAt ?: Timestamp.now())
        )
    }

    companion object {
        fun fromMap(map: Map<String, Any>): TourBooking {
            return TourBooking(
                id = map["id"] as? String ?: "",
                userId = map["userId"] as? String ?: "",
                propertyId = map["propertyId"] as? String ?: "",
                propertyOwnerId = map["propertyOwnerId"] as? String ?: "",
                propertyTitle = map["propertyTitle"] as? String ?: "",
                date = map["date"] as? String ?: "",
                time = map["time"] as? String ?: "",
                tourType = map["tourType"] as? String ?: "",
                status = map["status"] as? String ?: STATUS_PENDING,
                createdAt = map["createdAt"] as? Timestamp
            )
        }

        const val STATUS_PENDING = "PENDING"
        const val STATUS_CONFIRMED = "CONFIRMED"
        const val STATUS_CANCELLED = "CANCELLED"
        const val STATUS_COMPLETED = "COMPLETED"
        const val STATUS_REJECTED = "REJECTED"
    }
}
