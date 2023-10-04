package com.cornellappdev.android.eateryblue.ui.components.settings

import androidx.compose.material.Switch
import androidx.compose.material.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import com.cornellappdev.android.eateryblue.ui.theme.EateryBlue
import com.cornellappdev.android.eateryblue.ui.theme.GrayOne

@Composable
fun SwitchOption(
    title: String,
    description: String,
    onCheckedChange: (Boolean) -> Unit,
    enabled: Boolean = true,
    initialValue: Boolean = true
) {
    var switched by remember { mutableStateOf(initialValue) }
    SettingsOption(title = title, description = description, onClick = { },
        trailingIcon = {
            Switch(
                modifier = Modifier.scale(2f),
                checked = switched,
                onCheckedChange = {
                    switched = !switched
                    onCheckedChange(switched)
                },
                enabled = enabled,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    uncheckedThumbColor = Color.White,
                    checkedTrackColor = EateryBlue,
                    uncheckedTrackColor = GrayOne
                )
            )
        })
}
