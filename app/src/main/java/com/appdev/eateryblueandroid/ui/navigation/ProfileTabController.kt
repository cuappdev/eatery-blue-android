package com.appdev.eateryblueandroid.ui.screens

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import com.appdev.eateryblueandroid.ui.screens.settings.*
import com.appdev.eateryblueandroid.ui.viewmodels.*
import com.appdev.eateryblueandroid.util.*
import com.appdev.eateryblueandroid.util.Constants.passwordAlias
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@Composable
fun ProfileTabController(
    profileViewModel: ProfileViewModel,
    bottomSheetViewModel: BottomSheetViewModel,
    eateryState: State<HomeViewModel.State>,
    profileEateryDetailViewModel : EateryDetailViewModel
) {
    val display = profileViewModel.display.collectAsState()
    val state = profileViewModel.state.collectAsState()
    display.value.let {
        when (it) {
            is ProfileViewModel.Display.Login -> {
                if (CachedAccountInfo.cached) {
                    ProfileScreen(
                        profileViewModel = profileViewModel,
                        bottomSheetViewModel = bottomSheetViewModel
                    )
                    if (loadedUsername != null && loadedUsername!!.isNotEmpty()
                        && loadedPassword != null && loadedPassword!!.isNotEmpty() && !hasLoaded
                    ) {
                        hasLoaded = true
                        CoroutineScope(Dispatchers.Default).launch {
                            profileViewModel.autoLogin(
                                loadedUsername!!,
                                decryptData(passwordAlias, loadedPassword!!)
                            )
                            Log.i(
                                "Login",
                                "Attempting Login with username " + loadedUsername + " and password " + loadedPassword
                            )
                        }
                    }
                } else {
                    LoginScreen(
                        profileViewModel = profileViewModel
                    )
                }

            }
            is ProfileViewModel.Display.Profile ->
                ProfileScreen(
                    profileViewModel = profileViewModel,
                    bottomSheetViewModel = bottomSheetViewModel
                )
            is ProfileViewModel.Display.Settings ->
                SettingsScreen(profileViewModel)
            is ProfileViewModel.Display.About ->
                AboutScreen(profileViewModel)
            is ProfileViewModel.Display.Favorites ->
                FavoritesScreen(profileViewModel, eateryState, profileEateryDetailViewModel)
            is ProfileViewModel.Display.Notifications ->
                NotificationsScreen(profileViewModel)
            is ProfileViewModel.Display.Privacy ->
                PrivacyScreen(profileViewModel)
            is ProfileViewModel.Display.Legal ->
                LegalScreen(profileViewModel)
            is ProfileViewModel.Display.Support ->
                SupportScreen(profileViewModel)
            is ProfileViewModel.Display.EateryDetailVisible ->
                EateryDetailScreen(
                    eateryDetailViewModel = profileEateryDetailViewModel,
                    hideEatery = profileViewModel::transitionFavorites
                )

        }
    }
}