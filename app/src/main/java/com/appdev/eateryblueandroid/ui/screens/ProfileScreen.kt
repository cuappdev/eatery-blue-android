package com.appdev.eateryblueandroid.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.appdev.eateryblueandroid.R
import com.appdev.eateryblueandroid.models.AccountType
import com.appdev.eateryblueandroid.ui.components.core.Text
import com.appdev.eateryblueandroid.ui.components.core.TextStyle
import com.appdev.eateryblueandroid.ui.components.general.TopBar
import com.appdev.eateryblueandroid.ui.components.profile.Main
import com.appdev.eateryblueandroid.ui.components.profile.PaymentMethodSelector
import com.appdev.eateryblueandroid.ui.viewmodels.BottomSheetViewModel
import com.appdev.eateryblueandroid.ui.viewmodels.ProfileViewModel

@Composable
fun ProfileScreen(
    profileViewModel: ProfileViewModel,
    bottomSheetViewModel: BottomSheetViewModel
) {
    val state = profileViewModel.state.collectAsState()
    val showBottomSheet = {
        val currentFilter = (state.value as ProfileViewModel.State.ProfileData).accountFilter
        val updatedFilter = mutableStateOf(currentFilter)
        val toggleFilter = { filter: AccountType ->
            updatedFilter.value = filter
        }
        bottomSheetViewModel.show {
            PaymentMethodSelector(
                selectedFilter = updatedFilter,
                toggleFilter = toggleFilter,
                saveFilter = profileViewModel::updateAccountFilter,
                hide = bottomSheetViewModel::hide
            )
        }
    }
    Column {
        Box {
            TopBar(
                label = "Account",
                expanded = true,
                eateryIcon = false,
                rightIcon = null
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp, end = 16.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_settings),
                    tint = colorResource(id = R.color.white),
                    contentDescription = "Settings",
                    modifier = Modifier.clickable { profileViewModel.transitionSettings() }
                )
            }
        }


        state.value.let {
            when (it) {
                is ProfileViewModel.State.ProfileData ->
                    Main(
                        user = it.user,
                        accountFilter = it.accountFilter,
                        transactionQuery = it.query,
                        updateQuery = profileViewModel::updateQuery,
                        showBottomSheet = showBottomSheet
                    )
                is ProfileViewModel.State.LoggingIn ->
                    Text("FAILURE STATE")
                is ProfileViewModel.State.LoginFailure ->
                    Text("LOGIN FAILURE")
                is ProfileViewModel.State.Empty ->
                    Text("Internal Error")
            }
        }
    }
}