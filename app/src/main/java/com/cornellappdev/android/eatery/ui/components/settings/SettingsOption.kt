package com.cornellappdev.android.eatery.ui.components.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cornellappdev.android.eatery.ui.theme.EateryBlueTypography
import com.cornellappdev.android.eatery.ui.theme.GrayFive
import com.cornellappdev.android.eatery.ui.theme.GrayOne

@Composable
fun SettingsOption(
    title: String,
    onClick: () -> Unit = {},
    description: String? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
) {
    val interactionSource = remember { MutableInteractionSource() }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .clickable(
                onClick = { onClick() },
                interactionSource = interactionSource,
                indication = rememberRipple()
            ),
        verticalAlignment = CenterVertically
    ) {
        Row(
            verticalAlignment = CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            if (leadingIcon != null) {
                leadingIcon()
                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
            }

            Column {
                Text(
                    text = title,
                    style = EateryBlueTypography.h5,
                )
                if (!description.isNullOrEmpty())
                    Text(
                        text = description,
                        style = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 12.sp),
                        color = GrayFive,
                        modifier = Modifier.padding(top = 2.dp)
                    )
            }
        }
        if (trailingIcon != null) {
            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
            trailingIcon()
        }
    }
}

@Composable
fun SettingsLineSeparator() {
    Divider(color = GrayOne, modifier = Modifier.fillMaxWidth(), thickness = 1.dp)
}
