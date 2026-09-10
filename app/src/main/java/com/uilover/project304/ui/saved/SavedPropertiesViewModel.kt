package com.uilover.project304.ui.saved

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uilover.project304.data.model.Property
import com.uilover.project304.data.repository.AuthRepository
import com.uilover.project304.data.repository.PropertyRepository
import com.uilover.project304.data.repository.SavedRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SavedPropertiesViewModel @Inject constructor(
    private val savedRepository: SavedRepository,
    private val propertyRepository: PropertyRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _savedProperties = MutableStateFlow<List<Property>>(emptyList())
    val savedProperties: StateFlow<List<Property>> = _savedProperties.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private var currentUserId: String? = null

    init {
        loadSavedProperties()
    }

    private fun loadSavedProperties() {
        currentUserId = authRepository.currentUser?.uid
        if (currentUserId == null) {
            _errorMessage.value = "User not logged in"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            savedRepository.getFavoriteIds(currentUserId!!).collect { propertyIds ->
                val properties = mutableListOf<Property>()
                for (id in propertyIds) {
                    val property = propertyRepository.getPropertyById(id)
                    if (property != null && property.ownerId != currentUserId) {
                        properties.add(property)
                    }
                }
                _savedProperties.value = properties
                _isLoading.value = false
            }
        }
    }

    fun removeFromFavorites(propertyId: String) {
        if (currentUserId == null) return

        viewModelScope.launch {
            savedRepository.removeFromFavorites(currentUserId!!, propertyId)
        }
    }

    fun refreshSavedProperties() {
        loadSavedProperties()
    }
}
