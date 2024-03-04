package com.cornellappdev.android.eateryblue.ui.components.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cornellappdev.android.eateryblue.data.models.Eatery
import com.cornellappdev.android.eateryblue.ui.theme.EateryBlueTypography
import com.cornellappdev.android.eateryblue.ui.theme.GrayFive
import com.cornellappdev.android.eateryblue.ui.theme.GrayZero
import com.cornellappdev.android.eateryblue.ui.theme.Green
import com.cornellappdev.android.eateryblue.ui.theme.Red

/**
 * BottomSheet that displays the specific opening times of days in a week.
 * Also allows the user to switch to a bottom sheet where they can report
 * an issue or voice a concern.
 */
@Composable
fun EateryHourBottomSheet(
    eatery: Eatery,
    onDismiss: () -> Unit,
    onReportIssue: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 14.dp)
            .padding(16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row {
                Icon(
                    imageVector = Icons.Outlined.Schedule,
                    contentDescription = "Hours Icon",
                    tint = Color.Black,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                Text(
                    text = "Hours",
                    style = EateryBlueTypography.h4,
                    color = Color.Black
                )
            }
            IconButton(
                onClick = {
                    onDismiss()
                },
                modifier = Modifier
                    .size(40.dp)
                    .background(color = GrayZero, shape = CircleShape)
            ) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = Icons.Default.Close.name,
                    tint = Color.Black
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        val openUntil = eatery.getOpenUntil()
        Text(
            modifier = Modifier.padding(top = 2.dp),
            text = openUntil ?: "Closed",
            style = TextStyle(
                fontWeight = FontWeight.SemiBold, fontSize = 16.sp
            ),
            color = if (openUntil.isNullOrBlank()) Red else Green,
        )

        Spacer(modifier = Modifier.height(12.dp))

        val operatingHours = eatery.formatOperatingHours()

        operatingHours.forEach{(dayRange, hours) ->
            Column {
                Text(text = dayRange, fontSize = 16.sp, color = GrayFive,fontWeight = FontWeight(500))
                hours.forEach { hour ->
                    Text(
                        text = hour,
                        fontSize = 18.sp,
                        color = Color.Black,
                        fontWeight = FontWeight(600)
                    )
                }
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(12.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = onDismiss,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(backgroundColor = GrayZero),
            shape = RoundedCornerShape(corner = CornerSize(24.dp)),
        ) {
            Text("Close", color = Color.Black, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
        }

        Spacer(modifier = Modifier.height(12.dp))

        TextButton(
            onClick = onReportIssue,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                "Report an issue",
                color = Color.Black,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}