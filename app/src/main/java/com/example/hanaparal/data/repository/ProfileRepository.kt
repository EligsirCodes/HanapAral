package com.example.hanaparal.data.repository

import com.example.hanaparal.data.models.UserProfile
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await

class ProfileRepository(private val firestore: FirebaseFirestore) {
    private val usersCollection = firestore.collection("users")

    suspend fun saveUserProfile(profile: UserProfile): Boolean {
        return try {
            usersCollection.document(profile.uid)
                .set(profile.toMap(), SetOptions.merge())
                .await()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun fetchUserProfile(uid: String): UserProfile? {
        return try {
            val snapshot = usersCollection.document(uid).get().await()

            if (!snapshot.exists()) return null

            val name = snapshot.getString("name") ?: ""
            val email = snapshot.getString("email") ?: ""
            val course = snapshot.getString("course") ?: ""
            val year = snapshot.getString("year") ?: ""

            if (name.isNotBlank()) {
                UserProfile(
                    uid = uid,
                    name = name,
                    email = email,
                    course = course,
                    year = year
                )
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
}
