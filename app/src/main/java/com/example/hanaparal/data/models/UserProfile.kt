package com.example.hanaparal.data.models

import com.google.firebase.firestore.DocumentId

data class UserProfile(
    @DocumentId
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val course: String = "",
    val year: String = ""
) {
    fun toMap(): Map<String, Any> {
        return mapOf(
            "uid" to uid,
            "name" to name,
            "email" to email,
            "course" to course,
            "year" to year
        )
    }
}

