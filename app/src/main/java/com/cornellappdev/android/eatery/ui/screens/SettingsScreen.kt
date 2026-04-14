package com.cornellappdev.android.eatery.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.Gavel
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.cornellappdev.android.eatery.R
import com.cornellappdev.android.eatery.ui.components.settings.AppIconBottomSheet
import com.cornellappdev.android.eatery.ui.components.settings.SettingsLineSeparator
import com.cornellappdev.android.eatery.ui.components.settings.SettingsOption
import com.cornellappdev.android.eatery.ui.navigation.Routes
import com.cornellappdev.android.eatery.ui.theme.EateryBlue
import com.cornellappdev.android.eatery.ui.theme.EateryBlueTypography
import com.cornellappdev.android.eatery.ui.theme.GrayFive
import com.cornellappdev.android.eatery.ui.theme.GrayZero
import com.cornellappdev.android.eatery.ui.viewmodels.SettingsViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    settingsViewModel: SettingsViewModel = hiltViewModel(),
    destinations: HashMap<Routes, () -> Unit>
) {
    // To sign out, setIsLoggedIn to false and transition back to profileView with autoLogin false
    val coroutineScope = rememberCoroutineScope()
    val modalBottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
    )
    var showAppIconSheet by remember { mutableStateOf(false) }

    if (showAppIconSheet) {
        ModalBottomSheet(
            sheetState = modalBottomSheetState,
            onDismissRequest = { showAppIconSheet = false },
            shape = RoundedCornerShape(
                bottomStart = 0.dp,
                bottomEnd = 0.dp,
                topStart = 12.dp,
                topEnd = 12.dp
            )
        ) {
            AppIconBottomSheet {
                coroutineScope.launch {
                    modalBottomSheetState.hide()
                }.invokeOnCompletion {
                    if (!modalBottomSheetState.isVisible) showAppIconSheet = false
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp)
            .then(Modifier.statusBarsPadding())
    ) {
                Text(
                    text = stringResource(R.string.settings_title),
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
                    title = stringResource(R.string.settings_about_title),
                    description = stringResource(R.string.settings_about_description),
                    onClick = {
                        destinations[Routes.ABOUT]?.invoke()
                    }
                )
                SettingsOption(
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_appicon_settings),
                            contentDescription = null,
                            tint = GrayFive,
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    description = stringResource(R.string.settings_app_icon_description),
                    title = stringResource(R.string.settings_app_icon_title),
                    onClick = {
                        showAppIconSheet = true
                    },
                    trailingIcon = {
                        Text(
                            text = stringResource(R.string.settings_change),
                            style = EateryBlueTypography.button,
                            color = EateryBlue,
                        )
                    }
                )
                SettingsLineSeparator()
                SettingsOption(
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.StarOutline,
                            contentDescription = stringResource(R.string.settings_favorites_title),
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
                    title = stringResource(R.string.settings_favorites_title),
                    description = stringResource(R.string.settings_favorites_description),
                    onClick = {
                        destinations[Routes.FAVORITES]?.invoke()
                    }
                )
                SettingsLineSeparator()
                SettingsOption(
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_bell),
                            contentDescription = null,
                            tint = GrayFive,
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    title = stringResource(R.string.settings_notifications_title),
                    description = stringResource(R.string.settings_notifications_description),
                    onClick = {
                        destinations[Routes.NOTIFICATIONS_SETTING]?.invoke()
                    },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.ChevronRight,
                            contentDescription = null,
                            tint = EateryBlue,
                        )
                    },
                )
                SettingsLineSeparator()
                SettingsOption(
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.Lock,
                            contentDescription = stringResource(R.string.settings_privacy_title),
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
                    title = stringResource(R.string.settings_privacy_title),
                    description = stringResource(R.string.settings_privacy_description),
                    onClick = {
                        destinations[Routes.PRIVACY]?.invoke()
                    }
                )
                SettingsLineSeparator()
                SettingsOption(
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.Gavel,
                            contentDescription = stringResource(R.string.settings_legal_title),
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
                    title = stringResource(R.string.settings_legal_title),
                    description = stringResource(R.string.settings_legal_description),
                    onClick = {
                        destinations[Routes.LEGAL]?.invoke()
                    }
                )
                SettingsLineSeparator()
                SettingsOption(
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.HelpOutline,
                            contentDescription = stringResource(R.string.settings_support_title),
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
                    title = stringResource(R.string.settings_support_title),
                    description = stringResource(R.string.settings_support_description),
                    onClick = {
                        destinations[Routes.SUPPORT]?.invoke()
                    }
                )

                Spacer(modifier = Modifier.weight(1f))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 34.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = {
                            settingsViewModel.onLogout(onDone = {
                                destinations[Routes.PROFILE]?.invoke()
                            })
                        },
                        shape = RoundedCornerShape(25.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GrayZero,
                            contentColor = Color.Black
                        )
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Logout,
                            contentDescription = Icons.AutoMirrored.Filled.Logout.name,
                        )
                        Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                        Text(
                            text = stringResource(R.string.settings_logout),
                            style = EateryBlueTypography.button
                        )
                    }
                }
    }
}
