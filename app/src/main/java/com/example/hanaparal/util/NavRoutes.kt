package com.example.hanaparal.util

sealed class NavRoutes(val route: String) {
    data object Login : NavRoutes("login")
    data object ProfileSetup : NavRoutes("profile_setup")
    data object Home : NavRoutes("home")
    data object GroupList : NavRoutes("group_list")
    data object GroupDetail : NavRoutes("group_detail/{groupId}") {
        fun createRoute(groupId: String) = "group_detail/$groupId"
    }
    data object Announcement : NavRoutes("announcement/{groupId}") {
        fun createRoute(groupId: String) = "announcement/$groupId"
    }
}