package com.cornellappdev.android.eatery.ui.components.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.cornellappdev.android.eatery.R
import com.cornellappdev.android.eatery.data.models.ThemePreference
import com.cornellappdev.android.eatery.ui.theme.EateryBlueTypography
import com.cornellappdev.android.eatery.ui.theme.currentColors
import com.cornellappdev.android.eatery.util.DualModePreview
import com.cornellappdev.android.eatery.util.EateryPreview

@Composable
fun DisplayBottomSheet(
    themePreference: ThemePreference,
    onLightSelected: () -> Unit,
    onDarkSelected: () -> Unit,
    onSystemSelected: () -> Unit,
    onDismiss: () -> Unit
) {
    var selectedTheme by remember { mutableStateOf(themePreference) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 24.dp, bottom = 34.dp)
    ) {
        Text(
            text = stringResource(R.string.settings_display_title),
            style = EateryBlueTypography.h4,
            color = currentColors.textPrimary,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        DisplayThemeRow(
            icon = Icons.Outlined.WbSunny,
            label = stringResource(R.string.settings_display_light),
            selected = selectedTheme == ThemePreference.LIGHT,
            showTopDivider = false,
            onClick = { selectedTheme = ThemePreference.LIGHT }
        )
        DisplayThemeRow(
            icon = Icons.Outlined.DarkMode,
            label = stringResource(R.string.settings_display_dark),
            selected = selectedTheme == ThemePreference.DARK,
            showTopDivider = true,
            onClick = { selectedTheme = ThemePreference.DARK }
        )
        DisplayThemeRow(
            icon = Icons.Outlined.Settings,
            label = stringResource(R.string.settings_display_system),
            selected = selectedTheme == ThemePreference.SYSTEM,
            showTopDivider = true,
            onClick = { selectedTheme = ThemePreference.SYSTEM }
        )

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = {
                when (selectedTheme) {
                    ThemePreference.LIGHT -> onLightSelected()
                    ThemePreference.DARK -> onDarkSelected()
                    ThemePreference.SYSTEM -> onSystemSelected()
                }
                onDismiss()
            },
            shape = RoundedCornerShape(100.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = currentColors.backgroundSecondary,
                contentColor = currentColors.oppTextPrimary
            )
        ) {
            Text(
                text = stringResource(R.string.done),
                style = EateryBlueTypography.h5,
                color = currentColors.oppTextPrimary
            )
        }
    }
}

@Composable
private fun DisplayThemeRow(
    icon: ImageVector,
    label: String,
    selected: Boolean,
    showTopDivider: Boolean,
    onClick: () -> Unit
) {
    if (showTopDivider) {
        HorizontalDivider(
            color = currentColors.borderDefault,
            thickness = 1.dp
        )
    }
    val interactionSource = remember { MutableInteractionSource() }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(63.dp)
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(),
                onClick = onClick
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = currentColors.textSecondary
        )
        Text(
            text = label,
            style = EateryBlueTypography.h5,
            color = currentColors.textPrimary,
            modifier = Modifier.weight(1f)
        )
        RadioButton(
            selected = selected,
            onClick = onClick,
            colors = RadioButtonDefaults.colors(
                selectedColor = currentColors.textPrimary,
                unselectedColor = currentColors.borderDefault
            )
        )
    }
}

@DualModePreview
@Composable
private fun DisplayBottomSheetLightPreview() = EateryPreview {
    DisplayBottomSheet(
        themePreference = ThemePreference.LIGHT,
        onLightSelected = {},
        onDarkSelected = {},
        onSystemSelected = {},
        onDismiss = {}
    )
}

@DualModePreview
@Composable
private fun DisplayBottomSheetDarkPreview() = EateryPreview {
    DisplayBottomSheet(
        themePreference = ThemePreference.DARK,
        onLightSelected = {},
        onDarkSelected = {},
        onSystemSelected = {},
        onDismiss = {}
    )
}
