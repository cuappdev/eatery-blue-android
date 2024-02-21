package com.cornellappdev.android.eateryblue.ui.navigation

import com.cornellappdev.android.eateryblue.R

/**
 * Class to represent each tab.
 *
 * @property route matches the tab to the correct screen
 * @property unselectedIconId represents the resource id number for the tab icon when not selected
 * @property selectedIconId represents the resource id number for the tab icon when selected
 * @property selectedRoutes represents the routes the tab should be considered selected for
 */
sealed class NavigationItem(
    val route: String,
    val unselectedIconId: Int,
    val selectedIconId: Int,
    val selectedRoutes: Set<String>
) {
    object Home : NavigationItem(
        route = Routes.HOME.route,
        unselectedIconId = R.drawable.ic_home_unselected,
        selectedIconId = R.drawable.ic_home_selected,
        selectedRoutes = setOf(
            Routes.HOME.route,
            Routes.SEARCH.route,
            "${Routes.EATERY_DETAIL.route}/{eateryId}"
        )
    )

    object Upcoming : NavigationItem(
        route = Routes.UPCOMING.route,
        unselectedIconId = R.drawable.ic_calendar_unselected,
        selectedIconId = R.drawable.ic_calendar_selected,
        selectedRoutes = setOf(
            Routes.UPCOMING.route
        )
    )

    object Profile : NavigationItem(
        route = "${Routes.PROFILE.route}/true",
        unselectedIconId = R.drawable.ic_profile_unselected,
        selectedIconId = R.drawable.ic_profile_selected,
        selectedRoutes = setOf(
            "${Routes.PROFILE.route}/{autoLogin}",
            Routes.ACCOUNT.route,
            Routes.SETTINGS.route,
            Routes.ABOUT.route,
            Routes.FAVORITES.route,
            Routes.LEGAL.route,
            Routes.PRIVACY.route,
            Routes.SUPPORT.route
        )
    )

    companion object {
        val bottomNavTabList = listOf(
            Home,
            Upcoming,
            Profile

        )
    }
}

/**
 * All NavUnit must have a route (which specifies where to
 * navigate to).
 */
interface NavUnit {
    val route: String
}

/**
 * Contains information about all known routes. These should correspond to routes in our
 * NavHost/new routes should be added here. Routes can exist independent of tabs (like onboarding).
 */
enum class Routes(override var route: String) : NavUnit {
    HOME("home"),
    UPCOMING("upcoming"),
    PROFILE("profile"),
    ONBOARDING("onboarding"),
    EATERY_DETAIL("eatery_detail"),
    SEARCH("search"),
    SETTINGS("settings"),
    ACCOUNT("account"),
    ABOUT("about"),
    FAVORITES("favorites"),
    LEGAL("legal"),
    PRIVACY("privacy"),
    SUPPORT("support")
}
