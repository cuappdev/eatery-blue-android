package com.appdev.eateryblueandroid.ui.navigation

import android.content.Context
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.appdev.eateryblueandroid.R
import com.appdev.eateryblueandroid.ui.screens.HomeTabController
import com.appdev.eateryblueandroid.ui.screens.ProfileTabController
import com.appdev.eateryblueandroid.ui.viewmodels.*

//this composable makes the bottom nav bar and base layer on which different screens are shown
@Composable
fun MainScreen(
    context: Context,
    homeTabViewModel: HomeTabViewModel,
    homeViewModel: HomeViewModel,
    expandedSectionViewModel: ExpandedSectionViewModel,
    eateryDetailViewModel: EateryDetailViewModel,
    profileViewModel: ProfileViewModel,
    bottomSheetViewModel: BottomSheetViewModel
) {

    val navController = rememberNavController()
    val homeTab = BottomNavTab("home", "Home", R.drawable.ic_home)
    val profileTab = BottomNavTab("profile", "Profile", R.drawable.ic_user)
    val bottomNavItems = listOf(homeTab, profileTab)

    Scaffold(
        bottomBar = {
            BottomNav(navController, bottomNavItems)
        }
    ) {
        MainScreenNavigationConfigurations(
            navController,
            homeTab,
            profileTab,
            homeTabViewModel,
            homeViewModel,
            expandedSectionViewModel,
            eateryDetailViewModel,
            profileViewModel,
            bottomSheetViewModel
        )
    }
}

//this defines the behavior and look of each tab
@Composable
fun BottomNav(navController: NavHostController, items: List<BottomNavTab>) {
    BottomNavigation(backgroundColor = colorResource(id = R.color.white)) {
        val currentRoute = currentRoute(navController)
        items.forEach { screen ->
            BottomNavigationItem(
                icon = { Icon(painter = painterResource(id = screen.iconId), null) },
                label = { Text(screen.tabName) },
                selected = currentRoute == screen.route,
                selectedContentColor = colorResource(id = R.color.eateryBlue),
                unselectedContentColor = colorResource(id = R.color.gray05),
                onClick = {
                    if (currentRoute != screen.route) {
                        navController.navigate(screen.route)
                    }
                }
            )
        }
    }
}

@Composable
private fun currentRoute(navController: NavHostController): String? {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    return navBackStackEntry?.destination?.route
}

//this composable function takes care of mapping the routes defined by the tabs to their screens
@Composable
private fun MainScreenNavigationConfigurations(
    navController: NavHostController,
    homeTab: BottomNavTab, profileTab: BottomNavTab,
    homeTabViewModel: HomeTabViewModel,
    homeViewModel: HomeViewModel,
    expandedSectionViewModel: ExpandedSectionViewModel,
    eateryDetailViewModel: EateryDetailViewModel,
    profileViewModel: ProfileViewModel,
    bottomSheetViewModel: BottomSheetViewModel
) {
    val eateryListScrollState = rememberLazyListState()
    NavHost(navController = navController, startDestination = homeTab.route) {
        composable(homeTab.route) {
            HomeTabController(
                homeTabViewModel = homeTabViewModel,
                homeViewModel = homeViewModel,
                eateryDetailViewModel = eateryDetailViewModel,
                expandedSectionViewModel = expandedSectionViewModel,
                eateryListScrollState = eateryListScrollState,
                bottomSheetViewModel = bottomSheetViewModel
            )
        }
        composable(profileTab.route) {
            ProfileTabController(
                profileViewModel = profileViewModel,
                bottomSheetViewModel = bottomSheetViewModel
            )
        }
    }

}

//data class to represents each tab
//the route is needed to match the tab to the correct screen
//the tabName is the name of the tab
//the iconId is the resource id number for the drawable for the icon of the tab
data class BottomNavTab(val route: String, val tabName: String, val iconId: Int) 
