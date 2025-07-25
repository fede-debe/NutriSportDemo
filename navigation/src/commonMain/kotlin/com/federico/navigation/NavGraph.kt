package com.federico.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.federico.auth.AuthScreen
import com.federico.home.HomeGraphScreen
import com.federico.profile.ProfileScreen
import com.nutrisportdemo.shared.navigation.Screen

@Composable
fun SetupNavigation(startDestination: Screen = Screen.Auth) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = startDestination) {
        composable<Screen.Auth> {
            AuthScreen(navigateToHome = {
                navController.navigate(Screen.HomeGraph) {
                    /** Remove the auth screen from the backstack */
                    popUpTo<Screen.Auth> { inclusive = true }
                }
            })
        }
        composable<Screen.HomeGraph> {
            HomeGraphScreen(navigateToAuth = {
                navController.navigate(Screen.Auth) {
                    /** Remove the home graph screen from the backstack */
                    popUpTo<Screen.HomeGraph> { inclusive = true }
                }
            }, navigateToProfile = {
                navController.navigate(Screen.Profile)
            })
        }
        composable<Screen.Profile> {
            ProfileScreen()
        }
    }
}