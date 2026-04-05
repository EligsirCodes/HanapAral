package com.example.hanaparal.ui.nav

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.hanaparal.ui.auth.AuthViewModel
import com.example.hanaparal.ui.auth.LoginScreen
import com.example.hanaparal.ui.announcements.AnnouncementScreen
import com.example.hanaparal.ui.groups.GroupDetailScreen
import com.example.hanaparal.ui.groups.GroupDetailViewModel
import com.example.hanaparal.ui.groups.GroupListScreen
import com.example.hanaparal.ui.groups.GroupViewModel
import com.example.hanaparal.ui.home.HomeScreen
import com.example.hanaparal.ui.home.HomeViewModel
import com.example.hanaparal.ui.profile.ProfileScreen
import com.example.hanaparal.ui.profile.ProfileViewModel
import com.example.hanaparal.util.NavRoutes
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions

@Composable
fun AppNavHost(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    profileViewModel: ProfileViewModel,
    homeViewModel: HomeViewModel,
    groupViewModel: GroupViewModel,
    detailViewModel: GroupDetailViewModel
) {
    val currentUser = authViewModel.currentUser
    val startDestination = if (currentUser != null) NavRoutes.Home.route else NavRoutes.Login.route

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(NavRoutes.Login.route) {
            LoginScreen(
                viewModel = authViewModel,
                onLoginSuccess = {
                    val user = authViewModel.currentUser
                    user?.let {
                        profileViewModel.checkProfileExists(it.uid) { exists ->
                            val target = if (exists) NavRoutes.Home.route else NavRoutes.ProfileSetup.route
                            navController.navigate(target) {
                                popUpTo(NavRoutes.Login.route) { inclusive = true }
                            }
                        }
                    }
                }
            )
        }

        composable(NavRoutes.ProfileSetup.route) {
            ProfileScreen(
                viewModel = profileViewModel,
                onProfileSaved = {
                    navController.navigate(NavRoutes.Home.route) {
                        popUpTo(NavRoutes.ProfileSetup.route) { inclusive = true }
                    }
                }
            )
        }

        composable(NavRoutes.Home.route) {
            val context = LocalContext.current
            HomeScreen(
                viewModel = homeViewModel,
                onNavigateToGroups = { navController.navigate(NavRoutes.GroupList.route) },
                onSignOut = {
                    authViewModel.signOut()
                    homeViewModel.clearData()
                    detailViewModel.clearData()

                    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken("465527656248-u7mvjftneh31dgv83j5kvpsg6ocpr7qn.apps.googleusercontent.com")
                        .build()

                    GoogleSignIn.getClient(context, gso).signOut().addOnCompleteListener {
                        navController.navigate(NavRoutes.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                }
            )
        }

        composable(NavRoutes.GroupList.route) {
            GroupListScreen(
                viewModel = groupViewModel,
                onGroupClick = { id ->
                    navController.navigate(NavRoutes.GroupDetail.createRoute(id))
                }
            )
        }

        composable(
            route = NavRoutes.GroupDetail.route,
            arguments = listOf(navArgument("groupId") { type = NavType.StringType })
        ) { backStackEntry ->
            val groupId = backStackEntry.arguments?.getString("groupId") ?: ""
            GroupDetailScreen(
                groupId = groupId,
                viewModel = detailViewModel,
                onNavigateToAnnouncement = { id ->
                    navController.navigate(NavRoutes.Announcement.createRoute(id))
                }
            )
        }

        composable(
            route = NavRoutes.Announcement.route,
            arguments = listOf(navArgument("groupId") { type = NavType.StringType })
        ) { backStackEntry ->
            val groupId = backStackEntry.arguments?.getString("groupId") ?: ""
            AnnouncementScreen(
                groupId = groupId,
                viewModel = detailViewModel,
                onPostSuccess = { navController.popBackStack() }
            )
        }
    }
}