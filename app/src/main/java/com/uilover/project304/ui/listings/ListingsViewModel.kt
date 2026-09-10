package com.uilover.project304.ui.listings

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uilover.project304.data.model.Property
import com.uilover.project304.data.model.PropertyCategory
import com.uilover.project304.data.model.TourBooking
import com.uilover.project304.data.repository.AuthRepository
import com.uilover.project304.data.repository.PropertyRepository
import com.uilover.project304.data.repository.TourRepository
import com.uilover.project304.util.CloudinaryManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ListingsViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val propertyRepository: PropertyRepository,
    private val tourRepository: TourRepository,
    private val cloudinaryManager: CloudinaryManager
) : ViewModel() {
    private val userId = authRepository.currentUser?.uid

    private val _properties = MutableStateFlow<List<Property>>(emptyList())
    val properties: StateFlow<List<Property>> = _properties.asStateFlow()
    private val _receivedTours = MutableStateFlow<List<TourBooking>>(emptyList())
    val receivedTours: StateFlow<List<TourBooking>> = _receivedTours.asStateFlow()
    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving.asStateFlow()
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        userId?.let { id ->
            viewModelScope.launch {
                propertyRepository.getUserProperties(id).catch { _error.value = it.message }.collect { _properties.value = it }
            }
            viewModelScope.launch {
                tourRepository.getOwnerTours(id).catch { _error.value = it.message }.collect { _receivedTours.value = it }
            }
        }
    }

    fun property(propertyId: String): Property? = _properties.value.firstOrNull { it.id == propertyId }

    fun saveProperty(
        existing: Property?, title: String, price: String, address: String, beds: String, baths: String,
        sqft: String, category: PropertyCategory, description: String, amenities: String, image: Uri?, onSaved: () -> Unit
    ) {
        val owner = userId ?: run { _error.value = "User not logged in"; return }
        val parsedPrice = price.toDoubleOrNull()
        if (title.isBlank() || parsedPrice == null || parsedPrice <= 0 || address.isBlank() ||
            beds.toIntOrNull() == null || baths.toDoubleOrNull() == null || sqft.toIntOrNull() == null
        ) {
            _error.value = "Complete all required fields with valid numbers"
            return
        }
        if (existing == null && image == null) {
            _error.value = "Select a photo for the listing"
            return
        }
        _isSaving.value = true
        val id = existing?.id ?: UUID.randomUUID().toString()
        fun persist(imageUrl: String) {
            viewModelScope.launch {
                val property = Property(
                    id = id, title = title.trim(), price = parsedPrice,
                    formattedPrice = "$%,d".format(parsedPrice.toLong()), address = address.trim(),
                    beds = beds.toIntOrNull() ?: 0, baths = baths.toDoubleOrNull() ?: 0.0,
                    sqft = sqft.toIntOrNull() ?: 0, rating = existing?.rating ?: 0.0,
                    imageUrl = imageUrl, category = category, isFeatured = false,
                    badge = null, description = description.trim(),
                    amenities = amenities.split(",").map(String::trim).filter(String::isNotBlank),
                    ownerId = owner, isActive = existing?.isActive ?: true
                )
                val result = if (existing == null) propertyRepository.addProperty(property) else propertyRepository.updateProperty(property)
                _isSaving.value = false
                result.onSuccess { onSaved() }.onFailure { _error.value = it.message ?: "Unable to save listing" }
            }
        }
        if (image != null) {
            cloudinaryManager.uploadPropertyImage(image, id, ::persist) {
                _isSaving.value = false
                _error.value = it
            }
        } else {
            persist(existing?.imageUrl.orEmpty())
        }
    }

    fun setActive(property: Property) = viewModelScope.launch {
        propertyRepository.setPropertyActive(property.id, !property.isActive).onFailure { _error.value = it.message }
    }

    fun delete(propertyId: String) = viewModelScope.launch {
        propertyRepository.deleteProperty(propertyId).onFailure { _error.value = it.message }
    }

    fun respond(tourId: String, accepted: Boolean) = viewModelScope.launch {
        val result = tourRepository.respondToTour(tourId, accepted)
        result.onSuccess {
            val status = if (accepted) TourBooking.STATUS_CONFIRMED else TourBooking.STATUS_REJECTED
            _receivedTours.value = _receivedTours.value.map { if (it.id == tourId) it.copy(status = status) else it }
        }.onFailure { _error.value = it.message }
    }

    fun clearError() { _error.value = null }
}
