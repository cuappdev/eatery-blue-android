package com.cornellappdev.android.eatery.ui.components.details

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cornellappdev.android.eatery.R
import com.cornellappdev.android.eatery.data.models.Eatery
import com.cornellappdev.android.eatery.ui.theme.EateryBlueTypography
import com.cornellappdev.android.eatery.ui.theme.currentColors

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
    val colors = currentColors
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
                    contentDescription = null,
                    tint = currentColors.textPrimary,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                Text(
                    text = stringResource(R.string.hours_title),
                    style = EateryBlueTypography.h4,
                    color = currentColors.textPrimary
                )
            }
            IconButton(
                onClick = {
                    onDismiss()
                },
                modifier = Modifier
                    .size(40.dp)
                    .background(color = colors.backgroundDefault, shape = CircleShape)
            ) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = Icons.Default.Close.name,
                    tint = currentColors.textPrimary
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        val openUntil = eatery.getOpenUntil()
        Text(
            modifier = Modifier.padding(top = 2.dp),
            text = when {
                openUntil == null -> stringResource(R.string.closed)
                eatery.isClosingSoon() -> stringResource(R.string.closing_at, openUntil)
                else -> stringResource(R.string.open_until, openUntil)
            },
            style = TextStyle(
                fontWeight = FontWeight.SemiBold, fontSize = 16.sp
            ),
            color = if (openUntil == null) currentColors.error
            else if (eatery.isClosingSoon()) colors.accentPressed
            else currentColors.success
        )

        Spacer(modifier = Modifier.height(12.dp))

        val operatingHours = eatery.formatOperatingHours()

        operatingHours.forEach { (dayRange, hours) ->
            Column {
                Text(
                    text = dayRange,
                    fontSize = 16.sp,
                    color = colors.textSecondary,
                    fontWeight = FontWeight(500)
                )
                hours.forEach { hour ->
                    Text(
                        text = hour,
                        fontSize = 18.sp,
                        color = currentColors.textPrimary,
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
            colors = ButtonDefaults.buttonColors(containerColor = colors.backgroundDefault),
            shape = RoundedCornerShape(corner = CornerSize(24.dp)),
        ) {
            Text("Close", color = currentColors.textPrimary, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
            Text(
                stringResource(R.string.close),
                color = currentColors.textPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        TextButton(
            onClick = onReportIssue,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                stringResource(R.string.report_an_issue),
                color = currentColors.textPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
