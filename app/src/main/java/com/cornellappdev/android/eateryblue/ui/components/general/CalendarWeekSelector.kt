package com.cornellappdev.android.eateryblue.ui.components.general

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.cornellappdev.android.eateryblue.ui.theme.EateryBlue
import com.cornellappdev.android.eateryblue.ui.theme.EateryBlueTypography
import com.cornellappdev.android.eateryblue.ui.theme.GrayFive

/**
 * Reusable UI component that displays today and the next six days
 * Takes in an onClick function that takes in an integer (representing the
 * index of the day that was clicked) and runs when a selected day is clicked
 */
@Composable
fun CalendarWeekSelector(
    dayNames: List<String>,
    currSelectedDay: Int,
    selectedDay: Int,
    days: List<Int>,
    onClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(modifier),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        for (i in 0..6) {
            val size by animateFloatAsState(
                targetValue = if (currSelectedDay == i) 1.0f else 0f,
                label = "Circle Size"
            )
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = dayNames[i].uppercase(),
                    color = GrayFive,
                    textAlign = TextAlign.Center,
                    style = EateryBlueTypography.caption,
                    modifier = Modifier.padding(bottom = 8.dp),
                    fontWeight = FontWeight(600)
                )
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .clickable(
                            indication = null,
                            interactionSource = MutableInteractionSource()
                        ) { onClick(i) }
                        .size(34.dp)
                ) {
                    Surface(
                        modifier = Modifier.size(size = (34 * size).dp),
                        color = when (i) {
                            currSelectedDay -> if (currSelectedDay == selectedDay) EateryBlue else GrayFive
                            else -> Color.Transparent
                        },
                        shape = CircleShape
                    ) {}

                    Text(
                        text = days[i].toString(),
                        color =
                        if (i != currSelectedDay && i == selectedDay) EateryBlue
                        else if ((i == currSelectedDay || i == selectedDay)) Color.White
                        else Color.Black,
                        style = EateryBlueTypography.h6,
                        fontWeight = FontWeight.Normal,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(bottom = 3.dp)
                    )
                }
            }
        }
    }
}
