package com.cornellappdev.android.eatery.ui.components.settings

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.cornellappdev.android.eatery.ui.theme.currentColors
import com.cornellappdev.android.eatery.util.DualModePreview
import com.cornellappdev.android.eatery.util.EateryPreview

@Composable
fun SwitchOption(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    description: String? = null,
    enabled: Boolean = true,
    compact: Boolean = true,
) {
    SettingsOption(
        title = title,
        description = description,
        modifier = modifier,
        compact = compact,
        onClick = { if (enabled) onCheckedChange(!checked) },
        trailingIcon = {
            Switch(
                modifier = Modifier
                    .width(51.dp)
                    .height(31.dp),
                checked = checked,
                onCheckedChange = onCheckedChange,
                // Material 3 shrinks the unchecked thumb unless it has sized content; an
                // invisible spacer keeps it the same size in both states.
                thumbContent = { Spacer(modifier = Modifier.size(SwitchDefaults.IconSize)) },
                enabled = enabled,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = currentColors.backgroundDefault,
                    uncheckedThumbColor = currentColors.backgroundDefault,
                    checkedTrackColor = currentColors.contentBrand,
                    uncheckedTrackColor = currentColors.accentPrimary,
                    checkedBorderColor = Color.Transparent,
                    uncheckedBorderColor = Color.Transparent,
                ),
            )
        }
    )
}

@DualModePreview
@Composable
private fun SwitchOptionCheckedPreview() = EateryPreview {
    SwitchOption(
        title = "Favorite Item Notifications",
        description = "Get notified when your favorite items are served",
        checked = true,
        onCheckedChange = {}
    )
}

@DualModePreview
@Composable
private fun SwitchOptionUncheckedPreview() = EateryPreview {
    SwitchOption(
        title = "Pause All Notifications",
        checked = false,
        onCheckedChange = {}
    )
}

@DualModePreview
@Composable
private fun SwitchOptionDisabledPreview() = EateryPreview {
    SwitchOption(
        title = "Pause All Notifications",
        checked = false,
        onCheckedChange = {},
        enabled = false
    )
}