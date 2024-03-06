package com.cornellappdev.android.eateryblue.ui.components.general

import android.util.Log
import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.cornellappdev.android.eateryblue.ui.theme.EateryBlue
import com.cornellappdev.android.eateryblue.ui.theme.EateryBlueTypography
import com.cornellappdev.android.eateryblue.ui.theme.GrayFive
import java.time.LocalDate
import java.time.ZoneId

@Composable
fun CalendarWeekSelector(
    dayNames : List<String>,
    currSelectedDay:Int,
    selectedDay: Int,
    days : List<Int>,
    onClick : (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        for (i in 0..6) {
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
                        .padding(vertical = 8.dp, horizontal = 8.dp)
                        .clickable { onClick(i)}
                ) {
                    Surface(
                        modifier = Modifier.size(size = 34.dp),
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