package com.uilover.project304.ui.profile

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uilover.project304.data.model.UserProfile
import com.uilover.project304.data.repository.AuthRepository
import com.uilover.project304.data.repository.SavedRepository
import com.uilover.project304.data.repository.TourRepository
import com.uilover.project304.data.repository.UserRepository
import com.uilover.project304.util.CloudinaryManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val savedRepository: SavedRepository,
    private val tourRepository: TourRepository,
    private val cloudinaryManager: CloudinaryManager
) : ViewModel() {

    private val _userProfile = MutableStateFlow<UserProfile?>(null)
    val userProfile: StateFlow<UserProfile?> = _userProfile.asStateFlow()

    private val _savedCount = MutableStateFlow(0)
    val savedCount: StateFlow<Int> = _savedCount.asStateFlow()

    private val _tourCount = MutableStateFlow(0)
    val tourCount: StateFlow<Int> = _tourCount.asStateFlow()

    private val _isUpdating = MutableStateFlow(false)
    val isUpdating: StateFlow<Boolean> = _isUpdating.asStateFlow()

    private val _updateSuccess = MutableStateFlow(false)
    val updateSuccess: StateFlow<Boolean> = _updateSuccess.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private var currentUserId: String? = null

    init {
        loadUserProfile()
    }

    private fun loadUserProfile() {
        currentUserId = authRepository.currentUser?.uid
        if (currentUserId == null) {
            return
        }

        viewModelScope.launch {
            val profile = userRepository.getUserProfile(currentUserId!!)
            _userProfile.value = profile

            // Load counts
            val savedCount = savedRepository.getFavoriteCount(currentUserId!!)
            _savedCount.value = savedCount

            val tourCount = tourRepository.getUserTourCount(currentUserId!!)
            _tourCount.value = tourCount
        }
    }

    fun updateName(name: String) {
        if (currentUserId == null) return

        viewModelScope.launch {
            _isUpdating.value = true
            val result = userRepository.updateName(currentUserId!!, name)
            result.fold(
                onSuccess = {
                    _userProfile.value = _userProfile.value?.copy(name = name)
                    _updateSuccess.value = true
                    _isUpdating.value = false
                },
                onFailure = {
                    _errorMessage.value = it.message ?: "Failed to update name"
                    _isUpdating.value = false
                }
            )
        }
    }

    fun updatePhone(phone: String) {
        if (currentUserId == null) return

        viewModelScope.launch {
            _isUpdating.value = true
            val result = userRepository.updatePhone(currentUserId!!, phone)
            result.fold(
                onSuccess = {
                    _userProfile.value = _userProfile.value?.copy(phone = phone)
                    _updateSuccess.value = true
                    _isUpdating.value = false
                },
                onFailure = {
                    _errorMessage.value = it.message ?: "Failed to update phone"
                    _isUpdating.value = false
                }
            )
        }
    }

    fun updateBio(bio: String) {
        if (currentUserId == null) return

        viewModelScope.launch {
            _isUpdating.value = true
            val result = userRepository.updateBio(currentUserId!!, bio)
            result.fold(
                onSuccess = {
                    _userProfile.value = _userProfile.value?.copy(bio = bio)
                    _updateSuccess.value = true
                    _isUpdating.value = false
                },
                onFailure = {
                    _errorMessage.value = it.message ?: "Failed to update bio"
                    _isUpdating.value = false
                }
            )
        }
    }

    fun uploadAvatar(uri: Uri) {
        val uid = currentUserId ?: return

        _isUpdating.value = true
        cloudinaryManager.uploadAvatar(
            uri = uri,
            userId = uid,
            onSuccess = { url -> updateAvatar(url) },
            onError = {
                _errorMessage.value = it
                _isUpdating.value = false
            }
        )
    }

    fun updateAvatar(avatarUrl: String) {
        if (currentUserId == null) return

        viewModelScope.launch {
            _isUpdating.value = true
            val result = userRepository.updateAvatar(currentUserId!!, avatarUrl)
            result.fold(
                onSuccess = {
                    _userProfile.value = _userProfile.value?.copy(avatarUrl = avatarUrl)
                    _updateSuccess.value = true
                    _isUpdating.value = false
                },
                onFailure = {
                    _errorMessage.value = it.message ?: "Failed to update avatar"
                    _isUpdating.value = false
                }
            )
        }
    }

    fun signOut() {
        authRepository.signOut()
        _userProfile.value = null
    }

    fun refreshProfile() {
        loadUserProfile()
    }

    fun resetUpdateState() {
        _updateSuccess.value = false
        _errorMessage.value = null
    }
}
