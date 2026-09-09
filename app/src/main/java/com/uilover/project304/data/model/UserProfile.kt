package com.uilover.project304.data.model

import androidx.annotation.DrawableRes
import com.uilover.project304.R

data class UserProfile(
    val name: String = "Sophia Vance",
    val email: String = "sophia.vance@luxerealty.com",
    val membershipStatus: String = "VIP Platinum",
    @get:DrawableRes val avatarRes: Int = R.drawable.user_profile
)
