package com.example.hanaparal.ui.groups

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hanaparal.data.models.StudyGroup
import com.example.hanaparal.data.models.UserProfile
import com.example.hanaparal.data.repository.GroupRepository
import com.example.hanaparal.data.repository.ProfileRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GroupDetailViewModel(
    private val groupRepo: GroupRepository,
    private val profileRepo: ProfileRepository
) : ViewModel() {
    private val _currentGroup = MutableStateFlow<StudyGroup?>(null)
    val currentGroup: StateFlow<StudyGroup?> = _currentGroup.asStateFlow()
    private val _members = MutableStateFlow<List<UserProfile>>(emptyList())
    val members: StateFlow<List<UserProfile>> = _members.asStateFlow()
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun loadGroupAndMembers(groupId: String) {
        viewModelScope.launch {
            _isLoading.value = true

            val group = groupRepo.getGroupById(groupId)
            _currentGroup.value = group

            if (group != null) {
                val profileList = group.members.mapNotNull { memberId ->
                    profileRepo.fetchUserProfile(memberId)
                }
                _members.value = profileList
            }

            _isLoading.value = false
        }
    }

    fun clearData() {
        _currentGroup.value = null
        _members.value = emptyList()
    }

    fun postAnnouncement(groupId: String, title: String, body: String, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: return@launch

            val groupName = _currentGroup.value?.name ?: "Study Group"

            val success = groupRepo.postAnnouncement(
                groupId = groupId,
                senderId = currentUserId,
                groupName = groupName,
                title = title,
                body = body
            )
            onComplete(success)
        }
    }
}