package com.example.hanaparal.ui.profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hanaparal.data.models.UserProfile
import com.example.hanaparal.data.repository.ProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(private val repository: ProfileRepository) : ViewModel() {
    var name by mutableStateOf("")
    var course by mutableStateOf("")
    var year by mutableStateOf("")

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun saveProfile(uid: String, email: String, onComplete: (Boolean) -> Unit) {
        if (name.isBlank() || course.isBlank() || year.isBlank()) return

        viewModelScope.launch {
            _isLoading.value = true

            val profile = UserProfile(
                uid = uid,
                name = name.trim(),
                email = email,
                course = course.trim(),
                year = year
            )

            val success = repository.saveUserProfile(profile)
            _isLoading.value = false
            onComplete(success)
        }
    }

    fun checkProfileExists(uid: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            val profile = repository.fetchUserProfile(uid)
            _isLoading.value = false
            onResult(profile != null)
        }
    }
}