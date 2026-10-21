package com.cornellappdev.android.eatery.ui.components.settings

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.cornellappdev.android.eatery.ui.theme.currentColors

@Composable
fun SwitchOption(
    title: String,
    description: String? = null,
    onCheckedChange: (Boolean) -> Unit,
    checked: Boolean,
    modifier: Modifier = Modifier,
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
