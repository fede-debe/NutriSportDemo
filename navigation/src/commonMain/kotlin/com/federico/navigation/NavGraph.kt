package com.federico.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.federico.auth.AuthScreen
import com.federico.home.HomeGraphScreen

@Composable
fun SetupNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Screen.Auth) {
        composable<Screen.Auth> {
            AuthScreen()
        }
        composable<Screen.HomeGraph> {
            HomeGraphScreen()
        }
    }
}