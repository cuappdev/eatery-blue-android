package com.cornellappdev.android.eateryblue.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.outlined.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.cornellappdev.android.eateryblue.R
import com.cornellappdev.android.eateryblue.ui.components.settings.AppIconBottomSheet
import com.cornellappdev.android.eateryblue.ui.components.settings.SettingsLineSeparator
import com.cornellappdev.android.eateryblue.ui.components.settings.SettingsOption
import com.cornellappdev.android.eateryblue.ui.navigation.Routes
import com.cornellappdev.android.eateryblue.ui.theme.EateryBlue
import com.cornellappdev.android.eateryblue.ui.theme.EateryBlueTypography
import com.cornellappdev.android.eateryblue.ui.theme.GrayFive
import com.cornellappdev.android.eateryblue.ui.theme.GrayZero
import com.cornellappdev.android.eateryblue.ui.viewmodels.LoginViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SettingsScreen(
    loginViewModel: LoginViewModel,
    destinations: HashMap<Routes, () -> Unit>
) {
    // To sign out, setIsLoggedIn to false and transition back to profileView with autoLogin false
    val state = loginViewModel.state.collectAsState().value
    val coroutineScope = rememberCoroutineScope()
    val modalBottomSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
    )
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
            AppIconBottomSheet {
                coroutineScope.launch {
                    modalBottomSheetState.hide()
                }
            }
        },
        content = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 48.dp, start = 16.dp, end = 16.dp)
            ) {
                Text(
                    text = "Settings",
                    color = EateryBlue,
                    style = EateryBlueTypography.h2,
                    modifier = Modifier.padding(top = 7.dp, bottom = 7.dp)
                )
                SettingsOption(
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_appdev),
                            contentDescription = null,
                            tint = GrayFive,
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.ChevronRight,
                            contentDescription = null,
                            tint = EateryBlue,
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
                            imageVector = Icons.Outlined.StarOutline,
                            contentDescription = "Favorites",
                            tint = GrayFive,
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.ChevronRight,
                            contentDescription = null,
                            tint = EateryBlue,
                        )
                    },
                    title = "Favorites",
                    description = "Manage your favorite eateries and items",
                    onClick = {
                        destinations[Routes.FAVORITES]?.invoke()
                    }
                )
                SettingsLineSeparator()
                SettingsOption(
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_appicon_settings),
                            contentDescription = null,
                            tint = GrayFive,
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    title = "App Icon",
                    description = "Select the Eatery app icon for your phone",
                    onClick = {
                        coroutineScope.launch {
                            modalBottomSheetState.show()
                        }
                    },
                    trailingIcon = {
                        Text(
                            text = "Change",
                            style = EateryBlueTypography.button,
                            color = EateryBlue,
                        )
                    }
                )
                SettingsLineSeparator()
                SettingsOption(
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.Lock,
                            contentDescription = "Privacy",
                            tint = GrayFive,
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.ChevronRight,
                            contentDescription = null,
                            tint = EateryBlue,
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
                            tint = GrayFive,
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.ChevronRight,
                            contentDescription = null,
                            tint = EateryBlue,
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
                            tint = GrayFive,
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.ChevronRight,
                            contentDescription = null,
                            tint = EateryBlue,
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
                                color = GrayFive
                            )
                            Button(
                                onClick = {
                                    loginViewModel.onLogoutPressed()
                                    destinations[Routes.PROFILE]?.invoke()
                                },
                                shape = RoundedCornerShape(25.dp),
                                colors = ButtonDefaults.buttonColors(
                                    backgroundColor = GrayZero,
                                    contentColor = Color.Black
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
        })
}
