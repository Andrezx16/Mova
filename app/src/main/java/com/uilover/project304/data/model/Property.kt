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
    @get:DrawableRes val imageRes: Int = 0,
    val imageUrl: String = "",
    val category: PropertyCategory,
    val isFeatured: Boolean = false,
    val badge: String? = null,
    val isFavorite: Boolean = false,
    val description: String = "",
    val amenities: List<String> = emptyList(),
    val ownerId: String = "",
    val isActive: Boolean = true
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

    // Helper to check if we should use URL or drawable
    val hasImageUrl: Boolean
        get() = imageUrl.isNotEmpty()

    // Convert to Firestore map
    fun toMap(): Map<String, Any> {
        return mapOf(
            "id" to id,
            "title" to title,
            "price" to price,
            "address" to address,
            "beds" to beds,
            "baths" to baths,
            "sqft" to sqft,
            "rating" to rating,
            "imageUrl" to imageUrl,
            "category" to category.name,
            "isFeatured" to isFeatured,
            "badge" to (badge ?: ""),
            "description" to description,
            "amenities" to amenities,
            "ownerId" to ownerId,
            "isActive" to isActive
        )
    }

    companion object {
        fun fromMap(map: Map<String, Any>): Property {
            return Property(
                id = map["id"] as? String ?: "",
                title = map["title"] as? String ?: "",
                price = (map["price"] as? Number)?.toDouble() ?: 0.0,
                formattedPrice = "$%,d".format((map["price"] as? Number)?.toLong() ?: 0),
                address = map["address"] as? String ?: "",
                beds = (map["beds"] as? Number)?.toInt() ?: 0,
                baths = (map["baths"] as? Number)?.toDouble() ?: 0.0,
                sqft = (map["sqft"] as? Number)?.toInt() ?: 0,
                rating = (map["rating"] as? Number)?.toDouble() ?: 0.0,
                imageUrl = map["imageUrl"] as? String ?: "",
                category = PropertyCategory.valueOf(map["category"] as? String ?: "ALL"),
                isFeatured = map["isFeatured"] as? Boolean ?: false,
                badge = map["badge"] as? String,
                description = map["description"] as? String ?: "",
                amenities = map["amenities"] as? List<String> ?: emptyList(),
                ownerId = map["ownerId"] as? String ?: "",
                isActive = map["isActive"] as? Boolean ?: true
            )
        }
    }
}
