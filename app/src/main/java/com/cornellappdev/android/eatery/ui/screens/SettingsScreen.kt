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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.cornellappdev.android.eatery.R
import com.cornellappdev.android.eatery.ui.components.settings.AppIconBottomSheet
import com.cornellappdev.android.eatery.ui.components.settings.SettingsLineSeparator
import com.cornellappdev.android.eatery.ui.components.settings.SettingsOption
import com.cornellappdev.android.eatery.ui.navigation.Routes
import com.cornellappdev.android.eatery.ui.theme.EateryBlueTypography
import com.cornellappdev.android.eatery.ui.theme.currentColors
import com.cornellappdev.android.eatery.ui.viewmodels.SettingsViewModel
import com.cornellappdev.android.eatery.util.EateryPreview
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    settingsViewModel: SettingsViewModel = hiltViewModel(),
    destinations: HashMap<Routes, () -> Unit>
) {
    SettingsScreenContent(
        destinations = destinations,
        onLogout = {
            settingsViewModel.onLogout(onDone = {
                destinations[Routes.PROFILE]?.invoke()
            })
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsScreenContent(
    destinations: Map<Routes, () -> Unit>,
    onLogout: () -> Unit,
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
            .padding(horizontal = 16.dp)
            .then(Modifier.statusBarsPadding())
    ) {
        Text(
            text = stringResource(R.string.settings_title),
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
                    tint = currentColors.textSecondary,
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
                    color = currentColors.backgroundSecondary,
                )
            }
        )
        SettingsLineSeparator()
        SettingsOption(
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.StarOutline,
                    contentDescription = stringResource(R.string.settings_favorites_title),
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
                    tint = currentColors.textSecondary,
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
                    tint = currentColors.backgroundSecondary,
                )
            },
        )
        SettingsLineSeparator()
        SettingsOption(
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.Lock,
                    contentDescription = stringResource(R.string.settings_privacy_title),
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
                    onLogout()
                },
                shape = RoundedCornerShape(25.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = currentColors.backgroundDefault,
                    contentColor = currentColors.textPrimary
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

private fun previewDestinations(): Map<Routes, () -> Unit> = hashMapOf(
    Routes.ABOUT to {},
    Routes.FAVORITES to {},
    Routes.NOTIFICATIONS_SETTING to {},
    Routes.NOTIFICATIONS_HOME to {},
    Routes.LEGAL to {},
    Routes.PRIVACY to {},
    Routes.SUPPORT to {},
    Routes.PROFILE to {}
)

@Preview(showBackground = true)
@Composable
private fun SettingsScreenPreview() = EateryPreview {
    SettingsScreenContent(
        destinations = previewDestinations(),
        onLogout = {}
    )
}

