package com.uilover.project304.ui.details

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
class PropertyDetailViewModel @Inject constructor(
    private val propertyRepository: PropertyRepository,
    private val savedRepository: SavedRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _property = MutableStateFlow<Property?>(null)
    val property: StateFlow<Property?> = _property.asStateFlow()

    private val _isFavorite = MutableStateFlow(false)
    val isFavorite: StateFlow<Boolean> = _isFavorite.asStateFlow()
    val currentUserId: String? get() = authRepository.currentUser?.uid

    private var loadedPropertyId: String? = null

    fun loadProperty(propertyId: String) {
        if (loadedPropertyId == propertyId) return
        loadedPropertyId = propertyId

        viewModelScope.launch {
            _property.value = propertyRepository.getPropertyById(propertyId)

            val uid = authRepository.currentUser?.uid
            _isFavorite.value = if (uid != null) savedRepository.isFavorite(uid, propertyId) else false
        }
    }

    fun toggleFavorite() {
        val propertyId = loadedPropertyId ?: return
        val uid = authRepository.currentUser?.uid ?: return

        viewModelScope.launch {
            _isFavorite.value = savedRepository.toggleFavorite(uid, propertyId)
        }
    }
}
