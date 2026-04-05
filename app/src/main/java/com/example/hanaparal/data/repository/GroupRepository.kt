package com.example.hanaparal.data.repository

import com.example.hanaparal.data.models.StudyGroup
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class GroupRepository(private val firestore: FirebaseFirestore) {
    suspend fun createGroup(group: StudyGroup): Boolean {
        return try {
            firestore.collection("groups").add(group.toMap()).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun deleteGroup(groupId: String): Boolean {
        return try {
            firestore.collection("groups").document(groupId).delete().await()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun getGroupById(groupId: String): StudyGroup? {
        return try {
            val document = firestore.collection("groups").document(groupId).get().await()
            document.toObject(StudyGroup::class.java)
        } catch (e: Exception) {
            null
        }
    }

    fun getGroupsFlow(): Flow<List<StudyGroup>> = callbackFlow {
        val subscription = firestore.collection("groups")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    return@addSnapshotListener
                }

                val groups = snapshot?.toObjects(StudyGroup::class.java) ?: emptyList()
                trySend(groups)
            }
        awaitClose { subscription.remove() }
    }

    suspend fun joinGroup(groupId: String, groupName: String, userId: String, userName: String): Boolean {
        return try {
            firestore.collection("groups").document(groupId)
                .update("members", FieldValue.arrayUnion(userId))
                .await()

            addAlert(
                groupId = groupId,
                senderId = userId,
                title = groupName,
                message = "$userName has joined the group. Go and say hi!"
            )
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun leaveGroup(groupId: String, userId: String): Boolean {
        return try {
            firestore.collection("groups").document(groupId)
                .update("members", FieldValue.arrayRemove(userId))
                .await()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun postAnnouncement(
        groupId: String,
        senderId: String,
        groupName: String,
        title: String,
        body: String
    ): Boolean {
        return addAlert(
            groupId = groupId,
            senderId = senderId,
            title = "An announcement has been posted in $groupName",
            message = "$title: $body"
        )
    }

    private suspend fun addAlert(groupId: String, senderId: String, title: String, message: String): Boolean {
        return try {
            val alertData = hashMapOf(
                "title" to title,
                "message" to message,
                "senderId" to senderId,
                "timestamp" to Timestamp.now()
            )
            firestore.collection("groups").document(groupId)
                .collection("alerts")
                .add(alertData)
                .await()
            true
        } catch (e: Exception) {
            false
        }
    }
}