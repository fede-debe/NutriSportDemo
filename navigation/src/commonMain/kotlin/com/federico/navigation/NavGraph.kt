package com.federico.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.federico.admin_panel.AdminPanelScreen
import com.federico.auth.AuthScreen
import com.federico.home.HomeGraphScreen
import com.federico.manage_product.ManageProductScreen
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
            HomeGraphScreen(
                navigateToAuth = {
                    navController.navigate(Screen.Auth) {
                        /** Remove the home graph screen from the backstack */
                        popUpTo<Screen.HomeGraph> { inclusive = true }
                    }
                }, navigateToProfile = {
                    navController.navigate(Screen.Profile)
                },
                navigateToAdminPanel = {
                    navController.navigate(Screen.AdminPanel)
                })
        }
        composable<Screen.Profile> {
            ProfileScreen(
                navigateBack = {
                    /** pop our current screen from the back stack and navigate to the previous screen (HomeGraph) */
                    navController.navigateUp()
                }
            )
        }
        composable<Screen.AdminPanel> {
            AdminPanelScreen(
                navigateToManageProduct = { productId ->
                    navController.navigate(Screen.ManageProduct(id = productId))
                },
                navigateBack = {
                    navController.navigateUp()
                }
            )
        }
        composable<Screen.ManageProduct> { navBackStackEntry ->
            val productId = navBackStackEntry.toRoute<Screen.ManageProduct>().id
            ManageProductScreen(
                id = productId,
                navigateBack = {
                    navController.navigateUp()
                }
            )
        }
    }
}