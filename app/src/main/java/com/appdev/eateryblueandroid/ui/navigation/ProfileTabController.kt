package com.appdev.eateryblueandroid.ui.screens

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import com.appdev.eateryblueandroid.ui.viewmodels.BottomSheetViewModel
import com.appdev.eateryblueandroid.ui.viewmodels.ProfileViewModel
import com.appdev.eateryblueandroid.util.*
import com.appdev.eateryblueandroid.util.Constants.passwordAlias
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@Composable
fun ProfileTabController(
    profileViewModel: ProfileViewModel,
    bottomSheetViewModel: BottomSheetViewModel
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
        }
    }
}