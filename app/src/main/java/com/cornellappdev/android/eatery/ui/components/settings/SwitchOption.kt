package com.cornellappdev.android.eatery.ui.components.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.cornellappdev.android.eatery.ui.theme.EateryBlue
import com.cornellappdev.android.eatery.ui.theme.GrayOne

@Composable
fun SwitchOption(
    title: String,
    description: String,
    onCheckedChange: (Boolean) -> Unit,
    enabled: Boolean = true,
    initialValue: Boolean = true
) {
    var switched by remember { mutableStateOf(initialValue) }
    Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp)) {
        SettingsOption(
            title = title, description = description, onClick = { },
            trailingIcon = {
                Switch(
                    modifier = Modifier
                        .width(51.dp)
                        .height(31.dp),
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
                        uncheckedTrackColor = GrayOne,
                        checkedBorderColor = Color.Transparent,
                        uncheckedBorderColor = Color.Transparent,
                    ),
                )
            })
    }

}
