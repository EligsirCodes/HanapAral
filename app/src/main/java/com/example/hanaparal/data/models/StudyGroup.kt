package com.example.hanaparal.data.models

import com.google.firebase.firestore.DocumentId

data class StudyGroup(
    @DocumentId
    val id: String = "",
    val name: String = "",
    val subject: String = "",
    val description: String = "",
    val createdBy: String = "",
    val members: List<String> = emptyList()
) {
    fun toMap(): Map<String, Any> {
        return mapOf(
            "name" to name,
            "subject" to subject,
            "description" to description,
            "createdBy" to createdBy,
            "members" to members
        )
    }
}