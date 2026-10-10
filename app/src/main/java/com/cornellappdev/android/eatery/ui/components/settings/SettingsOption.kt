package com.cornellappdev.android.eatery.ui.components.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cornellappdev.android.eatery.ui.theme.EateryBlueTypography
import com.cornellappdev.android.eatery.ui.theme.currentColors

/**
 * A tappable settings row. [compact] gives the denser cell used on the notification and privacy
 * pages: smaller text, a secondary-colored description and height that wraps its content.
 */
@Composable
fun SettingsOption(
    title: String,
    onClick: () -> Unit = {},
    description: String? = null,
    modifier: Modifier = Modifier,
    compact: Boolean = false,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
) {
    val interactionSource = remember { MutableInteractionSource() }
    Row(
        modifier = modifier
            .background(color = currentColors.backgroundDefault)
            .fillMaxWidth()
            .then(if (compact) Modifier else Modifier.height(80.dp))
            .clickable(
                onClick = { onClick() },
                interactionSource = interactionSource,
                indication = ripple()
            )
            .then(if (compact) Modifier.padding(vertical = 16.dp) else Modifier),
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
                    style = if (compact) {
                        TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    } else {
                        EateryBlueTypography.h5
                    },
                    color = currentColors.textPrimary
                )
                if (!description.isNullOrEmpty())
                    Text(
                        text = description,
                        style = TextStyle(
                            fontWeight = if (compact) FontWeight.Medium else FontWeight.SemiBold,
                            fontSize = 12.sp
                        ),
                        color = if (compact) currentColors.textSecondary else currentColors.textPrimary,
                        modifier = Modifier.padding(top = if (compact) 4.dp else 2.dp)
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
    HorizontalDivider(
        color = currentColors.accentPrimary,
        modifier = Modifier.fillMaxWidth(),
        thickness = 1.dp
    )
}
