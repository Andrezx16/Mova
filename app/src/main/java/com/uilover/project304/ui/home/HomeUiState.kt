package com.uilover.project304.ui.home

import com.uilover.project304.data.mock.MockData
import com.uilover.project304.data.model.HomeNavTab
import com.uilover.project304.data.model.Property
import com.uilover.project304.data.model.PropertyCategory
import com.uilover.project304.data.model.UserProfile

data class HomeUiState(
    val searchQuery: String = "",
    val selectedCategory: PropertyCategory = PropertyCategory.ALL,
    val featuredProperties: List<Property> = MockData.featuredProperties,
    val recommendedProperties: List<Property> = MockData.recommendedProperties,
    val favoritePropertyIds: Set<String> = emptySet(),
    val selectedNavTab: HomeNavTab = HomeNavTab.HOME,
    val userProfile: UserProfile = MockData.currentUser,
    val isFilterDialogOpen: Boolean = false
) {
    val filteredRecommendedProperties: List<Property>
        get() {
            var list = recommendedProperties
            if (selectedCategory != PropertyCategory.ALL) {
                list = list.filter { it.category == selectedCategory }
            }
            if (searchQuery.isNotBlank()) {
                val query = searchQuery.trim().lowercase()
                list = list.filter {
                    it.title.lowercase().contains(query) ||
                    it.address.lowercase().contains(query) ||
                    it.category.name.lowercase().contains(query)
                }
            }
            return list
        }

    val filteredFeaturedProperties: List<Property>
        get() {
            var list = featuredProperties
            if (selectedCategory != PropertyCategory.ALL) {
                list = list.filter { it.category == selectedCategory }
            }
            if (searchQuery.isNotBlank()) {
                val query = searchQuery.trim().lowercase()
                list = list.filter {
                    it.title.lowercase().contains(query) ||
                    it.address.lowercase().contains(query) ||
                    it.category.name.lowercase().contains(query)
                }
            }
            return list
        }
}
