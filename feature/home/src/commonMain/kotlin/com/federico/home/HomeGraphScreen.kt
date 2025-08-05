package com.federico.home

import ContentWithMessageBar
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.federico.cart.CartScreen
import com.federico.home.component.BottomBar
import com.federico.home.component.CustomDrawer
import com.federico.home.domain.BottomBarDestination
import com.federico.home.domain.CustomDrawerState
import com.federico.home.domain.isOpened
import com.federico.home.domain.switchState
import com.federico.products_overview.ProductsOverviewScreen
import com.nutrisportdemo.shared.Alpha
import com.nutrisportdemo.shared.FontSize
import com.nutrisportdemo.shared.IconPrimary
import com.nutrisportdemo.shared.Resources
import com.nutrisportdemo.shared.Surface
import com.nutrisportdemo.shared.SurfaceLighter
import com.nutrisportdemo.shared.TextPrimary
import com.nutrisportdemo.shared.bebasNeueFont
import com.nutrisportdemo.shared.navigation.Screen
import com.nutrisportdemo.shared.util.getScreenWidth
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import rememberMessageBarState

/** We can't intercept the system back stack system in the current version of a compose navigation library to update the currentDestination accordingly
 * We are using the navController instead.
 *
 * - Observe destination while navigating with selectedDestination value.
 *      We use derivedStateOf because we create the new state from the currentDestination (existing state), and avoid extra recomposition that can occur.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeGraphScreen(
    navigateToAuth: () -> Unit,
    navigateToProfile: () -> Unit,
    navigateToAdminPanel: () -> Unit,
    navigateToDetails: (String) -> Unit
) {
    val viewModel = koinViewModel<HomeGraphViewModel>()
    val customer by viewModel.customer.collectAsState()
    val messageBarState = rememberMessageBarState()

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

    val screenWidth = remember { getScreenWidth() }
    var drawerState by remember { mutableStateOf(CustomDrawerState.Closed) }

    val offsetValue by remember { derivedStateOf { (screenWidth / 1.5).dp } }

    /** if drawer is closed we don't want to animate the home graph --> 0.dp */
    val animatedOffset by animateDpAsState(
        targetValue = if (drawerState.isOpened()) offsetValue else 0.dp
    )

    /** when drawer is opened the size of home graph is 90% of the screen width else full available size(1f) */
    val animatedScale by animateFloatAsState(
        targetValue = if (drawerState.isOpened()) 0.9f else 1f
    )

    val animatedRadius by animateDpAsState(
        targetValue = if (drawerState.isOpened()) 20.dp else 0.dp
    )

    val animatedBackground by animateColorAsState(
        targetValue = if (drawerState.isOpened()) SurfaceLighter else Surface
    )

    Box(modifier = Modifier.fillMaxSize().background(animatedBackground).systemBarsPadding()) {
        CustomDrawer(
            customer = customer,
            onProfileClick = navigateToProfile,
            onContactUsClick = {},
            onSignOutClick = {
                viewModel.signOut(
                    onSuccess = navigateToAuth,
                    onError = { message ->
                        messageBarState.addError(message)
                    }
                )
            },
            onAdminPanelClick = navigateToAdminPanel
        )
        /** we need extra box to provide modifier values to scale down the scaffold */
        Box(
            modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(animatedRadius))
                .offset(x = animatedOffset).scale(scale = animatedScale)
                .shadow(
                    elevation = 20.dp,
                    shape = RoundedCornerShape(animatedRadius),
                    ambientColor = Color.Black.copy(
                        Alpha.DISABLED
                    ),
                    spotColor = Color.Black.copy(
                        Alpha.DISABLED
                    )
                )
        ) {
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
                        AnimatedContent(targetState = drawerState) { drawer ->
                            when {
                                drawer.isOpened() -> {
                                    IconButton(onClick = {
                                        drawerState = drawerState.switchState()
                                    }) {
                                        Icon(
                                            painter = painterResource(Resources.Icon.Close),
                                            contentDescription = "Close icon",
                                            tint = IconPrimary
                                        )
                                    }
                                }

                                else -> {
                                    IconButton(onClick = {
                                        drawerState = drawerState.switchState()
                                    }) {
                                        Icon(
                                            painter = painterResource(Resources.Icon.Menu),
                                            contentDescription = "Menu icon",
                                            tint = IconPrimary
                                        )
                                    }
                                }
                            }
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
                ContentWithMessageBar(
                    modifier = Modifier.fillMaxSize().padding(
                        top = paddingValues.calculateTopPadding(),
                        bottom = paddingValues.calculateBottomPadding()
                    ),
                    messageBarState = messageBarState,
                    errorMaxLines = 2,
                    contentBackgroundColor = Surface
                ) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        NavHost(
                            modifier = Modifier.weight(1f),
                            navController = navController,
                            startDestination = Screen.ProductsOverview
                        ) {
                            composable<Screen.ProductsOverview> {
                                ProductsOverviewScreen(
                                    navigateToDetails = navigateToDetails
                                )
                            }
                            composable<Screen.Cart> { CartScreen() }
                            composable<Screen.Categories> {}
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        BottomBar(
                            selected = selectedDestination,
                            customer = customer,
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
    }
}