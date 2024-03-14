package com.cornellappdev.android.eateryblue.ui.components.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.cornellappdev.android.eateryblue.data.models.Eatery
import com.cornellappdev.android.eateryblue.ui.theme.EateryBlue
import com.cornellappdev.android.eateryblue.ui.theme.EateryBlueTypography
import com.cornellappdev.android.eateryblue.ui.theme.LightBlue
import java.time.LocalDateTime

/**
 * Alerts section of eatery details screen
 */
@Composable
fun AlertsSection(eatery: Eatery) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.padding(top = 12.dp)
    ) {

        eatery.alerts?.forEach {
            if (!it.description.isNullOrBlank() && it.startTime?.isBefore(LocalDateTime.now()) == true && it.endTime?.isAfter(
                    LocalDateTime.now()
                ) == true
            ) Surface(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .wrapContentSize(),
                shape = RoundedCornerShape(5.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(LightBlue)
                ) {
                    Icon(
                        Icons.Default.Info, contentDescription = "Warning", tint = EateryBlue
                    )
                    Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                    Text(
                        text = it.description,
                        style = EateryBlueTypography.body2,
                        color = EateryBlue,
                        modifier = Modifier.padding(start = 5.dp)
                    )
                }
            }
        }
    }
}