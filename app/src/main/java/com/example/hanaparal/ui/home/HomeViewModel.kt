package com.example.hanaparal.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hanaparal.data.models.UserProfile
import com.example.hanaparal.data.repository.ProfileRepository
import com.example.hanaparal.data.repository.AppConfigRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: ProfileRepository,
    private val configRepository: AppConfigRepository
) : ViewModel() {
    private val _userProfile = MutableStateFlow<UserProfile?>(null)
    val userProfile: StateFlow<UserProfile?> = _userProfile.asStateFlow()
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    val globalAnnouncement: StateFlow<String> = configRepository.globalAnnouncement

    fun loadUserData(uid: String) {
        if (_userProfile.value != null && _userProfile.value?.uid == uid) return

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val profile = repository.fetchUserProfile(uid)
                _userProfile.value = profile
            } catch (e: Exception) {
                _userProfile.value = null
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearData() {
        _userProfile.value = null
    }
}