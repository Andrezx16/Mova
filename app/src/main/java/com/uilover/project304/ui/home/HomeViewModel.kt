package com.uilover.project304.ui.home

import androidx.lifecycle.ViewModel
import com.uilover.project304.data.model.HomeNavTab
import com.uilover.project304.data.model.PropertyCategory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class HomeViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun onClearSearch() {
        _uiState.update { it.copy(searchQuery = "") }
    }

    fun onCategorySelected(category: PropertyCategory) {
        _uiState.update { it.copy(selectedCategory = category) }
    }

    fun onFavoriteToggle(propertyId: String) {
        _uiState.update { state ->
            val updatedFavorites = if (state.favoritePropertyIds.contains(propertyId)) {
                state.favoritePropertyIds - propertyId
            } else {
                state.favoritePropertyIds + propertyId
            }
            state.copy(favoritePropertyIds = updatedFavorites)
        }
    }

    fun onNavTabSelected(tab: HomeNavTab) {
        _uiState.update { it.copy(selectedNavTab = tab) }
    }

    fun onToggleFilterDialog() {
        _uiState.update { it.copy(isFilterDialogOpen = !it.isFilterDialogOpen) }
    }
}
