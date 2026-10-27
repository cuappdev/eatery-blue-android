package com.cornellappdev.android.eatery.ui.components.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import com.cornellappdev.android.eatery.ui.theme.currentColors
import com.cornellappdev.android.eatery.util.DualModePreview
import com.cornellappdev.android.eatery.util.EateryPreview

/**
 * A raised, rounded container for grouping settings cells. Use [CircleShape] for a pill-shaped
 * card holding a single cell.
 */
@Composable
fun SettingsCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(24.dp),
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 4.dp,
                shape = shape,
                ambientColor = Color.Black.copy(alpha = 0.12f),
                spotColor = Color.Black.copy(alpha = 0.12f)
            )
            .clip(shape)
            .background(currentColors.backgroundDefault)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.Center,
        content = content
    )
}

@DualModePreview
@Composable
private fun SettingsCardPreview() = EateryPreview {
    SettingsCard(modifier = Modifier.padding(16.dp)) {
        SwitchOption(
            title = "Favorite Item Notifications",
            description = "Get notified when your favorite items are served",
            checked = true,
            onCheckedChange = {}
        )
        SettingsLineSeparator()
        SwitchOption(
            title = "Cornell AppDev Notifications",
            description = "Get notified about new releases and feedback",
            checked = false,
            onCheckedChange = {}
        )
    }
}
