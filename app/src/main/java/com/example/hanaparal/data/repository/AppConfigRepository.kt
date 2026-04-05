package com.example.hanaparal.data.repository

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.remoteconfig.ConfigUpdate
import com.google.firebase.remoteconfig.ConfigUpdateListener
import com.google.firebase.remoteconfig.ConfigUpdateListenerRegistration
import com.google.firebase.remoteconfig.FirebaseRemoteConfigException
import com.google.firebase.remoteconfig.remoteConfig
import com.google.firebase.remoteconfig.remoteConfigSettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AppConfigRepository {
    private val remoteConfig = Firebase.remoteConfig
    private var configUpdateRegistration: ConfigUpdateListenerRegistration? = null
    private val _globalAnnouncement = MutableStateFlow("Welcome to HanapAral")
    val globalAnnouncement: StateFlow<String> = _globalAnnouncement.asStateFlow()
    private val _groupCreationEnabled = MutableStateFlow(true)
    val groupCreationEnabled: StateFlow<Boolean> = _groupCreationEnabled.asStateFlow()
    private val _maxMembersPerGroup = MutableStateFlow(5)
    val maxMembersPerGroup: StateFlow<Int> = _maxMembersPerGroup.asStateFlow()

    init {
        Firebase.remoteConfig.setDefaultsAsync(mapOf("debug" to true))

        val defaults = mapOf(
            "global_announcement" to "Welcome to HanapAral",
            "group_creation_enabled" to true,
            "max_members_per_group" to 5
        )
        remoteConfig.setDefaultsAsync(defaults)

        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 0
        }
        remoteConfig.setConfigSettingsAsync(configSettings)

        remoteConfig.fetchAndActivate().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("RemoteConfig", "Initial fetch successful")
                updateFlows()
            }
            startRealTimeListener()
        }
    }

    private fun startRealTimeListener() {
        configUpdateRegistration?.remove()
        Log.d("RemoteConfig", "Starting Real-Time Listener...")

        configUpdateRegistration = remoteConfig.addOnConfigUpdateListener(object : ConfigUpdateListener {
            override fun onUpdate(configUpdate: ConfigUpdate) {
                Log.d("RemoteConfig", "Signal Received Keys: ${configUpdate.updatedKeys}")
                remoteConfig.activate().addOnCompleteListener { task ->
                    if (task.isSuccessful) updateFlows()
                }
            }

            override fun onError(error: FirebaseRemoteConfigException) {
                Log.e("RemoteConfig", "Listener error: ${error.code}. Cause: ${error.cause?.message}")

                if (error.code == FirebaseRemoteConfigException.Code.CONFIG_UPDATE_STREAM_ERROR ||
                    error.message?.contains("Socket closed") == true) {

                    Log.w("RemoteConfig", "Socket closed. Retrying connection in 5s...")
                    android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                        startRealTimeListener()
                    }, 5000)
                }
            }
        })
    }

    private fun updateFlows() {
        _globalAnnouncement.value = remoteConfig.getString("global_announcement")
        _groupCreationEnabled.value = remoteConfig.getBoolean("group_creation_enabled")

        val maxMembers = remoteConfig.getDouble("max_members_per_group").toInt()
        _maxMembersPerGroup.value = if (maxMembers > 0) maxMembers else 5

        Log.d("RemoteConfig", "UI Flows updated: Announcement = ${_globalAnnouncement.value}")
    }
}