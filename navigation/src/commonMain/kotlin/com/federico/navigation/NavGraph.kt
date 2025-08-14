package com.federico.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.federico.admin_panel.AdminPanelScreen
import com.federico.auth.AuthScreen
import com.federico.category_search.CategorySearchScreen
import com.federico.checkout.CheckoutScreen
import com.federico.details.DetailsScreen
import com.federico.home.HomeGraphScreen
import com.federico.manage_product.ManageProductScreen
import com.federico.payment_completed.PaymentCompleted
import com.federico.profile.ProfileScreen
import com.nutrisportdemo.shared.domain.ProductCategory
import com.nutrisportdemo.shared.navigation.Screen
import com.nutrisportdemo.shared.util.IntentHandler
import org.koin.compose.koinInject

@Composable
fun SetupNavigation(startDestination: Screen = Screen.Auth) {
    val navController = rememberNavController()
    val intentHandler = koinInject<IntentHandler>()
    val navigateTo by intentHandler.navigateTo.collectAsState()

    LaunchedEffect(navigateTo) {
        navigateTo?.let { paymentCompleted ->
            println("NAVIGATE TO PAYMENT COMPLETED !!!")
            navController.navigate(paymentCompleted)
            intentHandler.resetNavigation()
        }
    }

//    val preferencesData by PreferencesRepository.readPayPalDataFlow()
//        .collectAsState(initial = null)
//
//    LaunchedEffect(preferencesData) {
//        preferencesData?.let { paymentCompleted ->
//            if(paymentCompleted.token != null) {
//                navController.navigate(paymentCompleted)
//                PreferencesRepository.reset()
//            }
//        }
//    }


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
                },
                navigateToDetails = { productId ->
                    navController.navigate(Screen.Details(productId = productId))
                },
                navigateToCategorySearch = { category ->
                    navController.navigate(Screen.CategorySearch(category = category))
                },
                navigateToCheckout = { totalAmount ->
                    navController.navigate(Screen.Checkout(totalAmount = totalAmount))
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
                    navController.navigate(Screen.ManageProduct(productId = productId))
                },
                navigateBack = {
                    navController.navigateUp()
                }
            )
        }
        composable<Screen.ManageProduct> { navBackStackEntry ->
            val productId = navBackStackEntry.toRoute<Screen.ManageProduct>().productId
            ManageProductScreen(
                id = productId,
                navigateBack = {
                    navController.navigateUp()
                }
            )
        }
        composable<Screen.Details> {
            DetailsScreen(
                navigateBack = {
                    /** pop our current screen from the back stack and navigate to the previous screen (HomeGraph) */
                    navController.navigateUp()
                }
            )
        }
        composable<Screen.CategorySearch> {
            val category = ProductCategory.valueOf(it.toRoute<Screen.CategorySearch>().category)
            CategorySearchScreen(
                category = category,
                navigateToDetails = { productId ->
                    navController.navigate(Screen.Details(productId = productId))
                },
                navigateBack = {
                    navController.navigateUp()
                }
            )
        }
        composable<Screen.Checkout> {
            val totalAmount = it.toRoute<Screen.Checkout>().totalAmount
            CheckoutScreen(
                totalAmount = totalAmount.toDoubleOrNull() ?: 0.0,
                navigateBack = {
                    navController.navigateUp()
                },
                navigateToPaymentCompleted = { isSuccess, error ->
                    navController.navigate(Screen.PaymentCompleted(isSuccess, error))
                }
            )
        }
        composable<Screen.PaymentCompleted> {
            PaymentCompleted(
                navigateBack = {
                    navController.navigate(Screen.HomeGraph) {
                        launchSingleTop = true
                        // Clear backstack completely
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}