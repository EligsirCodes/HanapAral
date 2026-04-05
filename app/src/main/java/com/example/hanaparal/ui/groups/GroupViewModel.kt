package com.example.hanaparal.ui.groups

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hanaparal.data.models.StudyGroup
import com.example.hanaparal.data.repository.AppConfigRepository
import com.example.hanaparal.data.repository.GroupRepository
import com.example.hanaparal.data.repository.ProfileRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class GroupViewModel(
    private val repository: GroupRepository,
    private val profileRepository: ProfileRepository,
    private val configRepository: AppConfigRepository
) : ViewModel() {
    val isGroupCreationEnabled: StateFlow<Boolean> = configRepository.groupCreationEnabled
    val maxMembersPerGroup: StateFlow<Int> = configRepository.maxMembersPerGroup

    val groups: StateFlow<List<StudyGroup>> = repository.getGroupsFlow()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun createGroup(name: String, subject: String, desc: String, userId: String) {
        if (!configRepository.groupCreationEnabled.value) return

        viewModelScope.launch {
            val newGroup = StudyGroup(
                name = name,
                subject = subject,
                description = desc,
                createdBy = userId,
                members = listOf(userId)
            )
            repository.createGroup(newGroup)
        }
    }

    fun deleteGroup(groupId: String) {
        viewModelScope.launch {
            repository.deleteGroup(groupId)
        }
    }

    fun joinGroup(group: StudyGroup, userId: String) {
        if (group.members.size >= configRepository.maxMembersPerGroup.value) return

        viewModelScope.launch {
            val userProfile = profileRepository.fetchUserProfile(userId)
            val studentName = userProfile?.name ?: "A Student"

            repository.joinGroup(
                groupId = group.id,
                groupName = group.name,
                userId = userId,
                userName = studentName
            )
        }
    }

    fun leaveGroup(groupId: String, userId: String) {
        viewModelScope.launch {
            repository.leaveGroup(groupId, userId)
        }
    }
}