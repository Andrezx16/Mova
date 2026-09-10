package com.uilover.project304.data.model

import androidx.annotation.DrawableRes
import com.google.firebase.Timestamp
import com.uilover.project304.R

data class UserProfile(
    val uid: String = "",
    val name: String = "Sophia Vance",
    val email: String = "sophia.vance@luxerealty.com",
    val phone: String = "",
    val membershipStatus: String = "VIP Platinum",
    @get:DrawableRes val avatarRes: Int = R.drawable.user_profile,
    val avatarUrl: String = "",
    val bio: String = "",
    val createdAt: Timestamp? = null
) {
    val hasAvatarUrl: Boolean
        get() = avatarUrl.isNotEmpty()

    fun toMap(): Map<String, Any> {
        return mapOf(
            "uid" to uid,
            "name" to name,
            "email" to email,
            "phone" to phone,
            "membershipStatus" to membershipStatus,
            "avatarUrl" to avatarUrl,
            "bio" to bio,
            "createdAt" to (createdAt ?: Timestamp.now())
        )
    }

    companion object {
        fun fromMap(map: Map<String, Any>): UserProfile {
            return UserProfile(
                uid = map["uid"] as? String ?: "",
                name = map["name"] as? String ?: "",
                email = map["email"] as? String ?: "",
                phone = map["phone"] as? String ?: "",
                membershipStatus = map["membershipStatus"] as? String ?: "",
                avatarUrl = map["avatarUrl"] as? String ?: "",
                bio = map["bio"] as? String ?: "",
                createdAt = map["createdAt"] as? Timestamp
            )
        }
    }
}
