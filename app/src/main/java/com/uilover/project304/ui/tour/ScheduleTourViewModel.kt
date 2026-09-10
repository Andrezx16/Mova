package com.uilover.project304.ui.tour

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.uilover.project304.data.model.Property
import com.uilover.project304.data.model.TourBooking
import com.uilover.project304.data.repository.AuthRepository
import com.uilover.project304.data.repository.PropertyRepository
import com.uilover.project304.data.repository.TourRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScheduleTourViewModel @Inject constructor(
    private val tourRepository: TourRepository,
    private val propertyRepository: PropertyRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _property = MutableStateFlow<Property?>(null)
    val property: StateFlow<Property?> = _property.asStateFlow()

    private val _isBookingSuccessful = MutableStateFlow(false)
    val isBookingSuccessful: StateFlow<Boolean> = _isBookingSuccessful.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private var currentUserId: String? = null
    private var loadedPropertyId: String? = null

    init {
        currentUserId = authRepository.currentUser?.uid
    }

    fun loadProperty(propertyId: String) {
        if (loadedPropertyId == propertyId) return
        loadedPropertyId = propertyId

        viewModelScope.launch {
            _property.value = propertyRepository.getPropertyById(propertyId)
        }
    }

    fun bookTour(
        propertyId: String,
        propertyTitle: String,
        date: String,
        time: String,
        tourType: String
    ) {
        if (currentUserId == null) {
            _errorMessage.value = "User not logged in"
            return
        }
        if (_property.value?.ownerId == currentUserId) {
            _errorMessage.value = "You cannot book a tour for your own listing"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            val tour = TourBooking(
                userId = currentUserId!!,
                propertyId = propertyId,
                propertyOwnerId = _property.value?.ownerId.orEmpty(),
                propertyTitle = propertyTitle,
                date = date,
                time = time,
                tourType = tourType,
                createdAt = Timestamp.now()
            )

            val result = tourRepository.createTourBooking(tour)
            result.fold(
                onSuccess = {
                    _isBookingSuccessful.value = true
                    _isLoading.value = false
                },
                onFailure = {
                    _errorMessage.value = it.message ?: "Failed to book tour"
                    _isLoading.value = false
                }
            )
        }
    }

    fun resetBookingState() {
        _isBookingSuccessful.value = false
        _errorMessage.value = null
    }
}
