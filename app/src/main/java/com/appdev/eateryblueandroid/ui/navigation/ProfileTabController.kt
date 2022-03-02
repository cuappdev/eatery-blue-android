package com.appdev.eateryblueandroid.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import com.appdev.eateryblueandroid.ui.viewmodels.BottomSheetViewModel
import com.appdev.eateryblueandroid.ui.viewmodels.ProfileViewModel

@Composable
fun ProfileTabController(
    profileViewModel: ProfileViewModel,
    bottomSheetViewModel: BottomSheetViewModel
) {
    val display = profileViewModel.display.collectAsState()
    val state = profileViewModel.state.collectAsState()
    display.value.let {
        when (it) {
            is ProfileViewModel.Display.Login ->
                LoginScreen(
                    profileViewModel = profileViewModel
                )
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