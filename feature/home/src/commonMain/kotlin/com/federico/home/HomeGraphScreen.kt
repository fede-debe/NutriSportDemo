package com.federico.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.federico.home.component.BottomBar
import com.federico.home.component.CustomDrawer
import com.federico.home.domain.BottomBarDestination
import com.nutrisportdemo.shared.FontSize
import com.nutrisportdemo.shared.IconPrimary
import com.nutrisportdemo.shared.Resources
import com.nutrisportdemo.shared.Surface
import com.nutrisportdemo.shared.SurfaceLighter
import com.nutrisportdemo.shared.TextPrimary
import com.nutrisportdemo.shared.bebasNeueFont
import com.nutrisportdemo.shared.navigation.Screen
import org.jetbrains.compose.resources.painterResource

/** We can't intercept the system back stack system in the current version of a compose navigation library to update the currentDestination accordingly
 * We are using the navController instead.
 *
 * - Observe destination while navigating with selectedDestination value.
 *      We use derivedStateOf because we create the new state from the currentDestination (existing state), and avoid extra recomposition that can occur.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeGraphScreen() {
    val navController = rememberNavController()

    val currentDestination = navController.currentBackStackEntryAsState()

    val selectedDestination by remember {
        derivedStateOf {
            val route = currentDestination.value?.destination?.route.toString()
            when {
                route.contains(BottomBarDestination.ProductsOverview.screen.toString()) -> BottomBarDestination.ProductsOverview
                route.contains(BottomBarDestination.Cart.screen.toString()) -> BottomBarDestination.Cart
                route.contains(BottomBarDestination.Categories.screen.toString()) -> BottomBarDestination.Categories
                else -> BottomBarDestination.ProductsOverview
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(SurfaceLighter).systemBarsPadding()) {
        CustomDrawer(
            onProfileClick = {},
            onContactUsClick = {},
            onSignOutClick = {},
            onAdminPanelClick = {}
        )
        Scaffold(containerColor = Surface, topBar = {
            CenterAlignedTopAppBar(
                title = {
                    AnimatedContent(targetState = selectedDestination) { destination ->
                        Text(
                            destination.title,
                            fontFamily = bebasNeueFont(),
                            fontSize = FontSize.LARGE,
                            color = TextPrimary
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = {}) {
                        Icon(
                            painter = painterResource(Resources.Icon.Menu),
                            contentDescription = "Menu icon",
                            tint = IconPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Surface,
                    scrolledContainerColor = Surface,
                    navigationIconContentColor = IconPrimary,
                    titleContentColor = TextPrimary,
                    actionIconContentColor = IconPrimary
                )
            )
        }) { paddingValues ->
            Column(
                modifier = Modifier.fillMaxSize().padding(
                    top = paddingValues.calculateTopPadding(),
                    bottom = paddingValues.calculateBottomPadding()
                )
            ) {
                NavHost(
                    modifier = Modifier.weight(1f),
                    navController = navController,
                    startDestination = Screen.ProductsOverview
                ) {
                    composable<Screen.ProductsOverview> {}
                    composable<Screen.Cart> {}
                    composable<Screen.Categories> {}
                }
                Spacer(modifier = Modifier.height(12.dp))
                BottomBar(
                    selected = selectedDestination,
                    onSelect = { destination ->
                        navController.navigate(destination.screen) {
                            /**
                             * Pop up to the start destination of the graph when navigate back from a top destination.
                             * launchSingleTop is being used to not repeat the same screen in the backstack.
                             *
                             * When pop from the ProductsOverview screen, inclusive = false make sure it is kept in the backstack.
                             */
                            launchSingleTop = true
                            popUpTo<Screen.ProductsOverview> {
                                saveState = true
                                inclusive = false
                            }
                            restoreState = true
                        }
                    })
            }
        }
    }
}