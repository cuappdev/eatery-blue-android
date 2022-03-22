package com.appdev.eateryblueandroid.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import com.appdev.eateryblueandroid.ui.viewmodels.BottomSheetViewModel
import com.appdev.eateryblueandroid.ui.viewmodels.ProfileViewModel
import com.appdev.eateryblueandroid.util.loadData
import com.appdev.eateryblueandroid.util.loadedPassword
import com.appdev.eateryblueandroid.util.loadedUsername
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
                LoginScreen(
                    profileViewModel = profileViewModel
                )
                CoroutineScope(Dispatchers.IO).launch {
                    val loadJob : Job = loadData()
                    loadJob.join()
                    if (loadedUsername != null && loadedUsername!!.isNotEmpty())
                        profileViewModel.initiateLogin(loadedUsername!!, loadedPassword!!)
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