package com.cornellappdev.android.eatery.ui.navigation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.with
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material3.NavigationBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.cornellappdev.android.eatery.ui.components.general.AppStoreRatingPopup
import com.cornellappdev.android.eatery.ui.screens.AboutScreen
import com.cornellappdev.android.eatery.ui.screens.CompareMenusScreen
import com.cornellappdev.android.eatery.ui.screens.EateryDetailScreen
import com.cornellappdev.android.eatery.ui.screens.FavoritesScreen
import com.cornellappdev.android.eatery.ui.screens.FirstTimeShown
import com.cornellappdev.android.eatery.ui.screens.HomeScreen
import com.cornellappdev.android.eatery.ui.screens.LegalScreen
import com.cornellappdev.android.eatery.ui.screens.NearestScreen
import com.cornellappdev.android.eatery.ui.screens.NotificationsHomeScreen
import com.cornellappdev.android.eatery.ui.screens.NotificationsSettingsScreen
import com.cornellappdev.android.eatery.ui.screens.OnboardingScreen
import com.cornellappdev.android.eatery.ui.screens.PrivacyScreen
import com.cornellappdev.android.eatery.ui.screens.ProfileScreen
import com.cornellappdev.android.eatery.ui.screens.SearchScreen
import com.cornellappdev.android.eatery.ui.screens.SettingsScreen
import com.cornellappdev.android.eatery.ui.screens.SupportScreen
import com.cornellappdev.android.eatery.ui.screens.UpcomingMenuScreen
import com.cornellappdev.android.eatery.ui.theme.EateryBlue
import com.cornellappdev.android.eatery.ui.viewmodels.LoginViewModel
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun NavigationSetup(hasOnboarded: Boolean) {
    val navController = rememberAnimatedNavController()
    val showBottomBar = rememberSaveable {
        mutableStateOf(false)
    }

    // Subscribe to navBackStackEntry, required to get current route
    val navBackStackEntry by navController.currentBackStackEntryAsState()

    when (navBackStackEntry?.destination?.route) {
        Routes.ONBOARDING.route -> {
            showBottomBar.value = false
        }

        Routes.ABOUT.route -> {
            showBottomBar.value = false
        }

        Routes.FAVORITES.route -> {
            showBottomBar.value = true
        }

        Routes.NOTIFICATIONS_SETTING.route -> {
            showBottomBar.value = false
        }

        Routes.NOTIFICATIONS_HOME.route -> {
            showBottomBar.value = false
        }

        Routes.PRIVACY.route -> {
            showBottomBar.value = false
        }

        Routes.LEGAL.route -> {
            showBottomBar.value = false
        }

        Routes.SUPPORT.route -> {
            showBottomBar.value = false
        }

        else -> {
            showBottomBar.value = true
        }
    }

    Scaffold(
        bottomBar = {
            AnimatedContent(
                targetState = showBottomBar.value,
                transitionSpec = {
                    expandVertically(
                        animationSpec = tween(durationMillis = 500),
                        expandFrom = Alignment.Bottom
                    ) with shrinkVertically(animationSpec = tween(durationMillis = 500))
                }
            ) { state ->
                if (state) {
                    BottomNavigationBar(navController, NavigationItem.bottomNavTabList)
                }
            }
        }
    ) { innerPadding ->
        SetupNavHost(
            modifier = Modifier.padding(innerPadding),
            hasOnboarded = hasOnboarded,
            navController = navController,
            showBottomBar = showBottomBar
        )
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController, tabItems: List<NavigationItem>) {
    NavigationBar(
        containerColor = Color.White,
        contentColor = EateryBlue
    ) {
        val currentRoute = currentRoute(navController)
        tabItems.forEach { item ->
            BottomNavigationItem(
                icon = {
                    Icon(
                        painter = painterResource(
                            id = if (item.selectedRoutes.contains(currentRoute)) item.selectedIconId else item.unselectedIconId
                        ),
                        contentDescription = item.route
                    )
                },
                selected = item.selectedRoutes.contains(currentRoute),
                selectedContentColor = Color.Unspecified,
                unselectedContentColor = Color.Unspecified,
                onClick = {
                    FirstTimeShown.firstTimeShown = false
                    navController.navigate(item.route)
                }
            )
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun SetupNavHost(
    modifier: Modifier = Modifier,
    hasOnboarded: Boolean,
    navController: NavHostController,
    showBottomBar: MutableState<Boolean>,
    loginViewModel: LoginViewModel = hiltViewModel(),
) {
    AppStoreRatingPopup(navigateToSupport = { navController.navigate(Routes.SUPPORT.route) })
    // The starting destination switches to onboarding if it isn't completed.
    AnimatedNavHost(
        modifier = modifier,
        navController = navController,
        startDestination = if (hasOnboarded) Routes.HOME.route else Routes.ONBOARDING.route
    ) {
        composable(
            Routes.ONBOARDING.route,
            enterTransition = {
                fadeIn(
                    initialAlpha = 0f,
                    animationSpec = tween(durationMillis = 500)
                )
            },
            exitTransition = {
                fadeOut(
                    animationSpec = tween(durationMillis = 500)
                )
            }) {
            OnboardingScreen(
                proceedHome = { navController.navigate(Routes.HOME.route) }
            )
        }
        composable(
            Routes.HOME.route,
            enterTransition = {
                fadeIn(
                    initialAlpha = 0f,
                    animationSpec = tween(durationMillis = 500)
                )
            },
            exitTransition = {
                fadeOut(
                    animationSpec = tween(durationMillis = 500)
                )
            }) {
            HomeScreen(showBottomBar = showBottomBar, onSearchClick = {
                FirstTimeShown.firstTimeShown = false
                navController.navigate(Routes.SEARCH.route)
            }, onEateryClick = {
                FirstTimeShown.firstTimeShown = false
                navController.navigate("${Routes.EATERY_DETAIL.route}/${it.id}")
            }, onFavoriteExpand = {
                navController.navigate(Routes.FAVORITES.route)
            }, onCompareMenusClick = { selectedEateries ->
                navController.navigate("comparemenus/${selectedEateries.joinToString(",") { it.toString() }}")
            },
                onNotificationsClick = {
                    navController.navigate("notifications_home")
                }
            )
        }
        composable(
            Routes.UPCOMING.route,
            enterTransition = {
                fadeIn(
                    initialAlpha = 0f,
                    animationSpec = tween(durationMillis = 500)
                )
            },
            exitTransition = {
                fadeOut(
                    animationSpec = tween(durationMillis = 500)
                )
            }) {
            UpcomingMenuScreen(onEateryClick = { eateryId ->
                navController.navigate("${Routes.EATERY_DETAIL.route}/$eateryId")
            })
        }
        composable(
            route = "${Routes.EATERY_DETAIL.route}/{eateryId}",
            arguments = listOf(navArgument("eateryId") { type = NavType.IntType }),
            enterTransition = {
                fadeIn(
                    initialAlpha = 0f,
                    animationSpec = tween(durationMillis = 500)
                )
            },
            exitTransition = {
                fadeOut(
                    animationSpec = tween(durationMillis = 500)
                )
            }) {
            EateryDetailScreen(
                onCompareMenusClick = { selectedEateriesIds ->
                    navController.navigate("comparemenus/${selectedEateriesIds.joinToString(",") { it.toString() }}")
                }
            )
        }
        composable(
            route = Routes.SEARCH.route,
            enterTransition = {
                fadeIn(
                    initialAlpha = 0f,
                    animationSpec = tween(durationMillis = 500)
                )
            },
            exitTransition = {
                fadeOut(
                    animationSpec = tween(durationMillis = 500)
                )
            }) {
            SearchScreen(onEateryClick = {
                navController.navigate("${Routes.EATERY_DETAIL.route}/${it.id}")
            }, onFavoriteClick = {
                navController.navigate(Routes.FAVORITES.route)
            })
        }
        composable(
            route = "${Routes.PROFILE.route}/{autoLogin}",
            arguments = listOf(navArgument("autoLogin") {
                type = NavType.BoolType
                defaultValue = true
            }),
            enterTransition = {
                fadeIn(
                    initialAlpha = 0f,
                    animationSpec = tween(durationMillis = 500)
                )
            },
            exitTransition = {
                fadeOut(
                    animationSpec = tween(durationMillis = 500)
                )
            }) {
            ProfileScreen(
                loginViewModel = loginViewModel,
                onSettingsClicked = { navController.navigate(Routes.SETTINGS.route) }
            )
        }
        composable(
            route = Routes.SETTINGS.route,
            enterTransition = {
                fadeIn(
                    initialAlpha = 0f,
                    animationSpec = tween(durationMillis = 500)
                )
            },
            exitTransition = {
                fadeOut(
                    animationSpec = tween(durationMillis = 500)
                )
            }) {
            SettingsScreen(
                destinations = hashMapOf(
                    Routes.ABOUT to { navController.navigate(Routes.ABOUT.route) },
                    Routes.FAVORITES to { navController.navigate(Routes.FAVORITES.route) },
                    Routes.NOTIFICATIONS_SETTING to { navController.navigate(Routes.NOTIFICATIONS_SETTING.route) },
                    Routes.NOTIFICATIONS_HOME to { navController.navigate(Routes.NOTIFICATIONS_HOME.route) },
                    Routes.LEGAL to { navController.navigate(Routes.LEGAL.route) },
                    Routes.PRIVACY to { navController.navigate(Routes.PRIVACY.route) },
                    Routes.SUPPORT to { navController.navigate(Routes.SUPPORT.route) },
                    Routes.PROFILE to {
                        navController.navigate("${Routes.PROFILE.route}/false") {
                            if (navController.isOnBackStack(Routes.ACCOUNT.route)) {
                                popUpTo(Routes.ACCOUNT.route) { inclusive = true }
                            } else {
                                popUpTo(Routes.SETTINGS.route) { inclusive = true }
                            }
                        }
                    }
                ),
                loginViewModel = loginViewModel
            )

        }
        composable(
            route = Routes.ACCOUNT.route,
            enterTransition = {
                fadeIn(
                    initialAlpha = 0f,
                    animationSpec = tween(durationMillis = 500)
                )
            },
            exitTransition = {
                fadeOut(
                    animationSpec = tween(durationMillis = 500)
                )
            }) { backStackEntry ->
            // TODO account page
        }
        composable(
            route = Routes.ABOUT.route,
            enterTransition = {
                fadeIn(
                    initialAlpha = 0f,
                    animationSpec = tween(durationMillis = 500)
                )
            },
            exitTransition = {
                fadeOut(
                    animationSpec = tween(durationMillis = 500)
                )
            }) {
            AboutScreen()
        }
        composable(
            route = Routes.FAVORITES.route,
            enterTransition = {
                fadeIn(
                    initialAlpha = 0f,
                    animationSpec = tween(durationMillis = 500)
                )
            },
            exitTransition = {
                fadeOut(
                    animationSpec = tween(durationMillis = 500)
                )
            }) {
            FavoritesScreen(onEateryClick = {
                navController.navigate("${Routes.EATERY_DETAIL.route}/${it.id}")
            }, onSearchClick = {
                FirstTimeShown.firstTimeShown = false
                navController.navigate(Routes.SEARCH.route)
            },
                onBackClick = {
                    navController.popBackStack()
                })
        }

        composable(
            route = Routes.NOTIFICATIONS_SETTING.route,
            enterTransition = {
                fadeIn(
                    initialAlpha = 0f,
                    animationSpec = tween(durationMillis = 500)
                )
            },
            exitTransition = {
                fadeOut(
                    animationSpec = tween(durationMillis = 500)
                )
            }) {
            NotificationsSettingsScreen()
        }

        composable(
            route = Routes.NOTIFICATIONS_HOME.route,
            enterTransition = {
                fadeIn(
                    initialAlpha = 0f,
                    animationSpec = tween(durationMillis = 500)
                )
            },
            exitTransition = {
                fadeOut(
                    animationSpec = tween(durationMillis = 500)
                )
            }) {
            NotificationsHomeScreen()
        }

        composable(
            route = Routes.NEAREST.route,
            enterTransition = {
                fadeIn(
                    initialAlpha = 0f,
                    animationSpec = tween(durationMillis = 500)
                )
            },
            exitTransition = {
                fadeOut(
                    animationSpec = tween(durationMillis = 500)
                )
            }) {
            NearestScreen(onEateryClick = {
                navController.navigate("${Routes.EATERY_DETAIL.route}/${it.id}")
            })
        }

        composable(
            route = Routes.LEGAL.route,
            enterTransition = {
                fadeIn(
                    initialAlpha = 0f,
                    animationSpec = tween(durationMillis = 500)
                )
            },
            exitTransition = {
                fadeOut(
                    animationSpec = tween(durationMillis = 500)
                )
            }) {
            LegalScreen()
        }
        composable(
            route = Routes.PRIVACY.route,
            enterTransition = {
                fadeIn(
                    initialAlpha = 0f,
                    animationSpec = tween(durationMillis = 500)
                )
            },
            exitTransition = {
                fadeOut(
                    animationSpec = tween(durationMillis = 500)
                )
            }) {
            PrivacyScreen()
        }
        composable(
            route = Routes.SUPPORT.route,
            enterTransition = {
                fadeIn(
                    initialAlpha = 0f,
                    animationSpec = tween(durationMillis = 500)
                )
            },
            exitTransition = {
                fadeOut(
                    animationSpec = tween(durationMillis = 500)
                )
            }) {
            SupportScreen()
        }
        composable(
            route = "comparemenus/{eateryIds}",
            arguments = listOf(navArgument("eateryIds") { type = NavType.StringType }),
            enterTransition = { fadeIn(animationSpec = tween(durationMillis = 500)) },
            exitTransition = { fadeOut(animationSpec = tween(durationMillis = 500)) }
        ) { backStackEntry ->
            backStackEntry.arguments?.getString("eateryIds")?.split(",")?.map { it.toInt() }
                ?.let { eateryIds ->
                    CompareMenusScreen(
                        eateryIds = eateryIds,
                        onEateryClick = {
                            FirstTimeShown.firstTimeShown = false
                            navController.navigate("${Routes.EATERY_DETAIL.route}/${it.id}")
                        }
                    )
                }
        }
    }
}

@Composable
private fun currentRoute(navController: NavHostController): String? {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    return navBackStackEntry?.destination?.route
}

fun NavController.isOnBackStack(route: String): Boolean = try {
    getBackStackEntry(route); true
} catch (e: Throwable) {
    false
}
