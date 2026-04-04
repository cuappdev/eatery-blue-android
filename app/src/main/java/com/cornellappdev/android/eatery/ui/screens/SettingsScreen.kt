package com.cornellappdev.android.eatery.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.Gavel
import androidx.compose.material.icons.outlined.HelpOutline
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.cornellappdev.android.eatery.R
import com.cornellappdev.android.eatery.data.repositories.UserPreferencesRepository
import com.cornellappdev.android.eatery.ui.components.settings.AppIconBottomSheet
import com.cornellappdev.android.eatery.ui.components.settings.SettingsLineSeparator
import com.cornellappdev.android.eatery.ui.components.settings.SettingsOption
import com.cornellappdev.android.eatery.ui.navigation.Routes
import com.cornellappdev.android.eatery.ui.theme.EateryBlueTypography
import com.cornellappdev.android.eatery.ui.theme.currentColors
import com.cornellappdev.android.eatery.ui.viewmodels.LoginViewModel
import com.cornellappdev.android.eatery.ui.viewmodels.ThemeViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SettingsScreen(
    loginViewModel: LoginViewModel,
    themeViewModel : ThemeViewModel,
    destinations: HashMap<Routes, () -> Unit>
) {
    // To sign out, setIsLoggedIn to false and transition back to profileView with autoLogin false
    val state = loginViewModel.state.collectAsState().value
    val coroutineScope = rememberCoroutineScope()
    val modalBottomSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
    )

    var modalSheetType by remember { mutableStateOf("") }
    ModalBottomSheetLayout(
        sheetState = modalBottomSheetState,
        sheetShape = RoundedCornerShape(
            bottomStart = 0.dp,
            bottomEnd = 0.dp,
            topStart = 12.dp,
            topEnd = 12.dp
        ),
        sheetElevation = 8.dp,
        sheetContent = {
            when (modalSheetType)
            {
                "appIcon"-> AppIconBottomSheet {
                    coroutineScope.launch {
                        modalBottomSheetState.hide()
                    }
                }
                "colorTheme" -> ThemeSheet {
                    coroutineScope.launch { modalBottomSheetState.hide() }
                }
                else ->
                {
                    Box(modifier = Modifier.height(1.dp))
                }

            }


        },
        content = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(currentColors.backgroundDefault)
            )
            {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 48.dp, start = 16.dp, end = 16.dp)

                ) {
                    Text(
                        text = "Settings",
                        color = currentColors.backgroundSecondary,
                        style = EateryBlueTypography.h2,
                        modifier = Modifier.padding(top = 7.dp, bottom = 7.dp)
                    )
                    SettingsOption(
                        leadingIcon = {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_appdev),
                                contentDescription = null,
                                tint = currentColors.textSecondary,
                                modifier = Modifier.size(24.dp)
                            )
                        },
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.ChevronRight,
                                contentDescription = null,
                                tint = currentColors.backgroundSecondary,
                            )
                        },
                        title = "About Eatery",
                        description = "Learn more about Cornell AppDev",
                        onClick = {
                            destinations[Routes.ABOUT]?.invoke()
                        }
                    )
                    SettingsLineSeparator()
                    SettingsOption(
                        leadingIcon = {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_appicon_settings),
                                contentDescription = null,
                                tint = currentColors.textSecondary,
                                modifier = Modifier.size(24.dp)
                            )
                        },
                        title = "App Icon",
                        description = "Select the Eatery app icon for your phone",
                        onClick = {
                            modalSheetType = "appIcon"
                            coroutineScope.launch {
                                modalBottomSheetState.show()
                            }
                        },
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.ChevronRight,
                                contentDescription = null,
                                tint = currentColors.backgroundSecondary,
                            )
                        }
                    )
                    SettingsLineSeparator()
                    SettingsOption(
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.StarOutline,
                                contentDescription = "Favorites",
                                tint = currentColors.textSecondary,
                                modifier = Modifier.size(24.dp)
                            )
                        },
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.ChevronRight,
                                contentDescription = null,
                                tint = currentColors.backgroundSecondary,
                            )
                        },
                        title = "Favorites",
                        description = "Manage your favorite eateries and items",
                        onClick = {
                            modalSheetType = "colorTheme"
                            coroutineScope.launch { modalBottomSheetState.show() }


                        }
                    )
                    SettingsLineSeparator()
                    SettingsOption(
                        leadingIcon = {
                            Icon(
                                painter = painterResource(id = R.drawable.light_mode),
                                contentDescription = null,
                                tint = currentColors.textSecondary,
                                modifier = Modifier.size(24.dp)

                            )
                        },
                        title = "Display",
                        description = "Choose a light or dark theme",
                        onClick = {
                            modalSheetType = "colorTheme"
                            coroutineScope.launch { modalBottomSheetState.show() }
                        },
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.ChevronRight,
                                contentDescription = null,
                                tint = currentColors.backgroundSecondary,
                            )
                        }
                    )
                    SettingsLineSeparator()
                    SettingsOption(
                        leadingIcon = {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_bell),
                                contentDescription = null,
                                tint = currentColors.textSecondary,
                                modifier = Modifier.size(24.dp)
                            )
                        },
                        title = "Notifications",
                        description = "Manage item and promotional notifications",
                        onClick = {
                            destinations[Routes.NOTIFICATIONS_SETTING]?.invoke()
                        },
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.ChevronRight,
                                contentDescription = null,
                                tint = currentColors.backgroundSecondary,
                            )
                        },
                    )
                    SettingsLineSeparator()
                    SettingsOption(
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.Lock,
                                contentDescription = "Privacy",
                                tint = currentColors.textSecondary,
                                modifier = Modifier.size(24.dp)
                            )
                        },
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.ChevronRight,
                                contentDescription = null,
                                tint = currentColors.backgroundSecondary,
                            )
                        },
                        title = "Privacy",
                        description = "Manage permissions and analytics",
                        onClick = {
                            destinations[Routes.PRIVACY]?.invoke()
                        }
                    )
                    SettingsLineSeparator()
                    SettingsOption(
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.Gavel,
                                contentDescription = "Legal",
                                tint = currentColors.textSecondary,
                                modifier = Modifier.size(24.dp)
                            )
                        },
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.ChevronRight,
                                contentDescription = null,
                                tint = currentColors.backgroundSecondary,
                            )
                        },
                        title = "Legal",
                        description = "Find terms, conditions, and privacy policy",
                        onClick = {
                            destinations[Routes.LEGAL]?.invoke()
                        }
                    )
                    SettingsLineSeparator()
                    SettingsOption(
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.HelpOutline,
                                contentDescription = "Support",
                                tint = currentColors.textSecondary,
                                modifier = Modifier.size(24.dp)
                            )
                        },
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.ChevronRight,
                                contentDescription = null,
                                tint = currentColors.backgroundSecondary,
                            )
                        },
                        title = "Support",
                        description = "Report issues and contact Cornell Appdev",
                        onClick = {
                            destinations[Routes.SUPPORT]?.invoke()
                        }
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    when (state) {
                        is LoginViewModel.State.Login -> {
                        }

                        is LoginViewModel.State.Account -> {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 34.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Logged in as ${state.user.userName!!.substringBefore('@')}",
                                    style = EateryBlueTypography.h5,
                                    color = currentColors.textSecondary
                                )
                                Button(
                                    onClick = {
                                        loginViewModel.onLogoutPressed()
                                        destinations[Routes.PROFILE]?.invoke()
                                    },
                                    shape = RoundedCornerShape(25.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        backgroundColor = currentColors.backgroundDefault,
                                        contentColor = currentColors.textPrimary
                                    )
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Logout,
                                        contentDescription = Icons.Default.Logout.name,
                                    )
                                    Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                                    Text(
                                        text = "Log out",
                                        style = EateryBlueTypography.button
                                    )
                                }
                            }
                        }
                    }
                }
            }
        })
}
