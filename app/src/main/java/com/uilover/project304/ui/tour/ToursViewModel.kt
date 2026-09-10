package com.uilover.project304.ui.tour

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uilover.project304.data.model.TourBooking
import com.uilover.project304.data.repository.AuthRepository
import com.uilover.project304.data.repository.TourRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ToursViewModel @Inject constructor(
    authRepository: AuthRepository,
    private val tourRepository: TourRepository
) : ViewModel() {

    private val _tours = MutableStateFlow<List<TourBooking>>(emptyList())
    val tours: StateFlow<List<TourBooking>> = _tours.asStateFlow()

    private val _updatingTourId = MutableStateFlow<String?>(null)
    val updatingTourId: StateFlow<String?> = _updatingTourId.asStateFlow()

    private val _isClearingHistory = MutableStateFlow(false)
    val isClearingHistory: StateFlow<Boolean> = _isClearingHistory.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        authRepository.currentUser?.uid?.let { userId ->
            viewModelScope.launch {
                tourRepository.getUserTours(userId)
                    .catch { error -> _errorMessage.value = error.message ?: "Unable to load tours" }
                    .collect { _tours.value = it }
            }
        }
    }

    fun cancelTour(tourId: String) {
        updateTour(tourId, isComplete = false)
    }

    fun completeTour(tourId: String) {
        updateTour(tourId, isComplete = true)
    }

    private fun updateTour(tourId: String, isComplete: Boolean) {
        viewModelScope.launch {
            _updatingTourId.value = tourId
            val result = if (isComplete) {
                tourRepository.completeTour(tourId)
            } else {
                tourRepository.cancelTour(tourId)
            }
            result.onFailure { error ->
                _errorMessage.value = error.message ?: "Unable to update tour"
            }
            result.onSuccess {
                val status = if (isComplete) TourBooking.STATUS_COMPLETED else TourBooking.STATUS_CANCELLED
                _tours.value = _tours.value.map { if (it.id == tourId) it.copy(status = status) else it }
            }
            _updatingTourId.value = null
        }
    }

    fun deleteTour(tourId: String) {
        viewModelScope.launch {
            _updatingTourId.value = tourId
            val result = tourRepository.deleteTour(tourId)
            result.onSuccess { _tours.value = _tours.value.filterNot { it.id == tourId } }
            result.onFailure { _errorMessage.value = it.message ?: "Unable to delete tour" }
            _updatingTourId.value = null
        }
    }

    fun clearHistory() {
        val historyIds = _tours.value
            .filter { it.status in terminalStatuses }
            .map { it.id }
        if (historyIds.isEmpty()) return

        viewModelScope.launch {
            _isClearingHistory.value = true
            val result = tourRepository.deleteTours(historyIds)
            result.onSuccess { _tours.value = _tours.value.filterNot { it.id in historyIds } }
            result.onFailure { _errorMessage.value = it.message ?: "Unable to clear history" }
            _isClearingHistory.value = false
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }

    private companion object {
        val terminalStatuses = setOf(
            TourBooking.STATUS_CANCELLED,
            TourBooking.STATUS_COMPLETED,
            TourBooking.STATUS_REJECTED
        )
    }
}
