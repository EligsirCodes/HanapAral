package com.example.hanaparal.ui.auth

import androidx.lifecycle.ViewModel
import com.example.hanaparal.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AuthViewModel(
    private val repository: AuthRepository
) : ViewModel() {
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    val currentUser get() = repository.currentUser

    fun signInWithFirebase(credential: AuthCredential, onResult: (Boolean) -> Unit) {
        _isLoading.value = true

        repository.signInWithCredential(credential) { success ->
            _isLoading.value = false
            onResult(success)
        }
    }

    fun signOut() {
        repository.signOut()
    }
}