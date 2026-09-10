package com.uilover.project304.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uilover.project304.data.model.HomeNavTab
import com.uilover.project304.data.model.Property
import com.uilover.project304.data.model.PropertyCategory
import com.uilover.project304.data.repository.AuthRepository
import com.uilover.project304.data.repository.PropertyRepository
import com.uilover.project304.data.repository.SavedRepository
import com.uilover.project304.util.FirestoreSeeder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val propertyRepository: PropertyRepository,
    private val savedRepository: SavedRepository,
    private val authRepository: AuthRepository,
    private val firestoreSeeder: FirestoreSeeder
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private var currentUserId: String? = null

    init {
        currentUserId = authRepository.currentUser?.uid
        seedDatabaseIfEmpty()
        loadProperties()
        loadFavoriteIds()
    }

    private fun seedDatabaseIfEmpty() {
        viewModelScope.launch {
            if (!propertyRepository.hasAnyProperties()) {
                firestoreSeeder.seedDatabase()
            }
        }
    }

    private fun loadProperties() {
        viewModelScope.launch {
            propertyRepository.getFeaturedProperties(currentUserId).collect { properties ->
                _uiState.update { it.copy(featuredProperties = properties) }
            }
        }

        viewModelScope.launch {
            propertyRepository.getRecommendedProperties(currentUserId).collect { properties ->
                _uiState.update { it.copy(recommendedProperties = properties) }
            }
        }
    }

    private fun loadFavoriteIds() {
        if (currentUserId == null) return

        viewModelScope.launch {
            savedRepository.getFavoriteIds(currentUserId!!).collect { propertyIds ->
                _uiState.update { it.copy(favoritePropertyIds = propertyIds.toSet()) }
            }
        }
    }

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
        if (currentUserId == null) return

        viewModelScope.launch {
            savedRepository.toggleFavorite(currentUserId!!, propertyId)
        }
    }

    fun onNavTabSelected(tab: HomeNavTab) {
        _uiState.update { it.copy(selectedNavTab = tab) }
    }

    fun onToggleFilterDialog() {
        _uiState.update { it.copy(isFilterDialogOpen = !it.isFilterDialogOpen) }
    }

    fun refreshProperties() {
        loadProperties()
        loadFavoriteIds()
    }
}
