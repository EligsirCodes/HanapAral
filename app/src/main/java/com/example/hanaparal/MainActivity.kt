package com.example.hanaparal

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.navigation.compose.rememberNavController
import com.example.hanaparal.data.repository.AppConfigRepository
import com.example.hanaparal.data.repository.AuthRepository
import com.example.hanaparal.data.repository.GroupRepository
import com.example.hanaparal.data.repository.ProfileRepository
import com.example.hanaparal.ui.auth.AuthViewModel
import com.example.hanaparal.ui.groups.GroupDetailViewModel
import com.example.hanaparal.ui.groups.GroupViewModel
import com.example.hanaparal.ui.home.HomeViewModel
import com.example.hanaparal.ui.nav.AppNavHost
import com.example.hanaparal.ui.profile.ProfileViewModel
import com.example.hanaparal.ui.theme.HanapAralTheme
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class MainActivity : FragmentActivity() {
    private var alertsListener: ListenerRegistration? = null
    private var authStateListener: FirebaseAuth.AuthStateListener? = null
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val auth = FirebaseAuth.getInstance()

        val authRepo = AuthRepository(auth)
        val profileRepo = ProfileRepository(db)
        val groupRepo = GroupRepository(db)

        val appConfigRepo = AppConfigRepository()

        val authViewModel = AuthViewModel(authRepo)
        val profileViewModel = ProfileViewModel(profileRepo)

        val homeViewModel = HomeViewModel(profileRepo, appConfigRepo)
        val groupViewModel = GroupViewModel(groupRepo, profileRepo, appConfigRepo)

        val detailViewModel = GroupDetailViewModel(groupRepo, profileRepo)

        checkNotificationPermission()

        setContent {
            HanapAralTheme {
                val navController = rememberNavController()

                AppNavHost(
                    navController = navController,
                    authViewModel = authViewModel,
                    profileViewModel = profileViewModel,
                    homeViewModel = homeViewModel,
                    groupViewModel = groupViewModel,
                    detailViewModel = detailViewModel
                )
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val auth = FirebaseAuth.getInstance()

        authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user != null) {
                startListeningForGlobalAlerts(user.uid)
            } else {
                alertsListener?.remove()
            }
        }
        auth.addAuthStateListener(authStateListener!!)
    }

    override fun onStop() {
        super.onStop()
        authStateListener?.let { FirebaseAuth.getInstance().removeAuthStateListener(it) }
    }

    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) !=
                PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 101)
            }
        }
    }

    private fun startListeningForGlobalAlerts(userId: String) {
        alertsListener?.remove()

        val bufferTime = Timestamp(Timestamp.now().seconds - 10, 0)

        alertsListener = db.collectionGroup("alerts")
            .whereGreaterThan("timestamp", bufferTime)
            .addSnapshotListener { snapshots, error ->
                if (error != null) {
                    Log.e("ALERTS", "Listener failed", error)
                    return@addSnapshotListener
                }

                for (change in snapshots?.documentChanges ?: emptyList()) {
                    if (change.type == DocumentChange.Type.ADDED) {
                        val title = change.document.getString("title") ?: "HanapAral Update"
                        val message = change.document.getString("message") ?: ""
                        val senderId = change.document.getString("senderId") ?: ""

                        if (senderId != userId) {
                            showSimpleNotification(title, message)
                        }
                    }
                }
            }
    }

    private fun showSimpleNotification(title: String, message: String) {
        val channelId = "hanaparal_notifs"
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Study Group Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for group activities"
            }
            notificationManager.createNotificationChannel(channel)
        }

        val builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setAutoCancel(true)

        notificationManager.notify(System.currentTimeMillis().toInt(), builder.build())
    }

    override fun onDestroy() {
        super.onDestroy()
        alertsListener?.remove()
    }
}