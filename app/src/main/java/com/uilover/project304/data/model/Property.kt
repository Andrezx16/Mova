package com.uilover.project304.data.model

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.uilover.project304.R

data class Property(
    val id: String,
    val title: String,
    val price: Double,
    val formattedPrice: String,
    val address: String,
    val beds: Int,
    val baths: Double,
    val sqft: Int,
    val rating: Double,
    @get:DrawableRes val imageRes: Int,
    val category: PropertyCategory,
    val isFeatured: Boolean = false,
    val badge: String? = null,
    val isFavorite: Boolean = false,
    val description: String = "",
    val amenities: List<String> = emptyList()
) {
    val formattedBeds: String
        @Composable
        get() = if (beds == 0) stringResource(R.string.category_studio) else if (beds == 1) stringResource(R.string.beds_count_one) else stringResource(R.string.beds_count_many, beds)

    val formattedBaths: String
        @Composable
        get() {
            val formatted = if (baths % 1.0 == 0.0) baths.toInt().toString() else baths.toString()
            return if (formatted == "1") stringResource(R.string.baths_count_one) else stringResource(R.string.baths_count_many, formatted)
        }

    val formattedSqft: String
        @Composable
        get() = stringResource(R.string.sqft_count, "%,d".format(sqft))
}
