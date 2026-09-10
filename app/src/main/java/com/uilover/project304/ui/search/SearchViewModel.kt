package com.uilover.project304.ui.search

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
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val propertyRepository: PropertyRepository,
    private val savedRepository: SavedRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _properties = MutableStateFlow<List<Property>>(emptyList())
    val properties: StateFlow<List<Property>> = _properties.asStateFlow()

    private val _favoriteIds = MutableStateFlow<Set<String>>(emptySet())
    val favoriteIds: StateFlow<Set<String>> = _favoriteIds.asStateFlow()

    private val currentUserId: String? get() = authRepository.currentUser?.uid

    init {
        viewModelScope.launch {
            propertyRepository.getAllProperties(currentUserId).collect { list ->
                _properties.update { list }
            }
        }

        currentUserId?.let { uid ->
            viewModelScope.launch {
                savedRepository.getFavoriteIds(uid).collect { ids ->
                    _favoriteIds.update { ids.toSet() }
                }
            }
        }
    }

    fun toggleFavorite(propertyId: String) {
        val uid = currentUserId ?: return
        viewModelScope.launch {
            savedRepository.toggleFavorite(uid, propertyId)
        }
    }
}
