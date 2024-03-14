package com.cornellappdev.android.eateryblue.ui.components.home

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cornellappdev.android.eateryblue.data.models.Eatery
import com.cornellappdev.android.eateryblue.ui.components.general.CalendarWeekSelector
import com.cornellappdev.android.eateryblue.ui.theme.EateryBlue
import com.cornellappdev.android.eateryblue.ui.theme.EateryBlueTypography
import com.cornellappdev.android.eateryblue.ui.theme.GrayFive
import com.cornellappdev.android.eateryblue.ui.theme.GrayZero
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.ZoneId

/**
 * BottomSheet that allows the menu displayed to be switched to a future
 * date.
 */
@Composable
fun EateryMenusBottomSheet(
    weekDayIndex: Int,
    mealType: Int,
    onDismiss: () -> Unit,
    eatery: Eatery,
    onShowMenuClick: (Int, String, Int) -> Unit,
//    onResetClick: (Int) -> Unit
) {
    val zoneId: ZoneId? = ZoneId.of("America/New_York")
    val today = LocalDate.now(zoneId)
    val currentDay by remember { mutableStateOf(today) }
//    Log.d("current day", currentDay.dayOfWeek.value.toString())
//    Log.d("current day2", currentDay.dayOfMonth.toString())
    val dayWeek: Int = currentDay.dayOfWeek.value
    val dayNum: Int = currentDay.dayOfMonth
    val dayNames = mutableListOf<String>()
    val dayWeeks = mutableListOf<Int>()
    val days = mutableListOf<Int>()
    dayWeeks.add(dayWeek)
    days.add(dayNum)
    for (i in 1 until 7) {
        dayWeeks.add(currentDay.plusDays(i.toLong()).dayOfWeek.value)
        days.add(currentDay.plusDays(i.toLong()).dayOfMonth)
    }
//    Log.d("list for cal", dayWeeks.toList().toString())
    dayWeeks.forEach {
        var dayName = ""
        when (it) {
            1 -> dayName = "Mon"
            2 -> dayName = "Tue"
            3 -> dayName = "Wed"
            4 -> dayName = "Thu"
            5 -> dayName = "Fri"
            6 -> dayName = "Sat"
            7 -> dayName = "Sun"
        }
        dayNames.add(dayName)
    }
    var selectedDay by remember { mutableStateOf(weekDayIndex) }
    var currSelectedDay by remember { mutableStateOf(selectedDay) }

    val closedDays: List<DayOfWeek> = eatery.getClosedDays()
    Log.d("closed days", closedDays.toString())
    val closedDaysStrings: List<String> = closedDays.map { dayOfWeek ->
        when (dayOfWeek.value) {
            1 -> "Mon"
            2 -> "Tue"
            3 -> "Wed"
            4 -> "Thu"
            5 -> "Fri"
            6 -> "Sat"
            7 -> "Sun"
            else -> "error"
        }
    }

    val selectedDayOfWeek = DayOfWeek.of(dayWeeks[currSelectedDay])
    val mealTypes: List<Pair<String, String>>? = eatery.getTypeMeal(selectedDayOfWeek)
    var selectedMealType by remember {
        mutableStateOf(mealTypes?.get(mealType)?.first ?: "")
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
    ) {
        Column(
            modifier = Modifier.padding(
                top = 15.dp, start = 15.dp, end = 15.dp
            ),
        ) {
            // Menus & X
            Row(
                modifier = Modifier.padding(bottom = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Menus",
                        style = EateryBlueTypography.h4,
                    )
                }
                IconButton(
                    onClick = {
//                        openUpcoming = false
                        onDismiss()
                    },
                    modifier = Modifier
                        .padding(all = 8.dp)
                        .background(color = GrayZero, shape = CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close Upcoming",
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            // Upcoming Day selector
            CalendarWeekSelector(
                dayNames = dayNames,
                currSelectedDay = currSelectedDay,
                selectedDay = selectedDay,
                days = days,
                onClick = { i ->
                    currSelectedDay = i
                    selectedMealType = mealTypes?.firstOrNull()?.first ?: ""
                },
                modifier = Modifier.padding(bottom = 12.dp),
                closedDays = closedDaysStrings
            )

//            Spacer(modifier = Modifier.height(12.dp))

            //display of possible meal descriptions (none for cafes)
            Column(modifier = Modifier
                .padding(top = 12.dp, bottom = 12.dp)
                .fillMaxWidth()) {
                if (mealTypes != null && mealTypes.size > 1) {
                    mealTypes?.forEach { (description, duration) ->
                        if (description != null && duration != null) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = description,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Black
                                    )
                                    Text(
                                        text = duration,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Gray
                                    )
                                }
                                IconButton(onClick = { selectedMealType = description }) {
                                    if (selectedMealType == description) {
                                        Box(
                                            contentAlignment = Alignment.Center,
                                            modifier = Modifier
                                                .size(24.dp)
                                                .background(Color.Black, CircleShape)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = "Selected",
                                                tint = Color.White
                                            )
                                        }
                                    } else {
                                        Box(
                                            contentAlignment = Alignment.Center,
                                            modifier = Modifier
                                                .size(24.dp)
                                                .background(Color.White, CircleShape)
                                                .border(2.dp, Color.Black, CircleShape)
                                        ) {
                                        }
                                    }
                                }

                            }
                        }
                    }
                }
            }


//            Spacer(modifier = Modifier.height(12.dp))

            // Show menu and reset menu buttons
            Column(
                modifier = Modifier.padding(bottom = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = {
                        selectedDay = currSelectedDay
                        if (mealTypes != null) {
                            onShowMenuClick(
                                currSelectedDay,
                                selectedMealType,
                                mealTypes.indexOfFirst { it.first == selectedMealType })
                        }
                        onDismiss()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(100),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = EateryBlue, contentColor = Color.White
                    )
                ) {
                    Text(
                        text = "Show menu",
                        style = EateryBlueTypography.h5,
                        color = Color.White
                    )
                }
                ClickableText(modifier = Modifier.padding(top = 12.dp),
                    text = AnnotatedString("Reset"),
                    style = TextStyle(
                        fontSize = 14.sp,
                        lineHeight = 17.5.sp,
                        fontWeight = FontWeight(600),
                        color = Color(0xFF050505)
                    ),
                    onClick = { selectedDay = weekDayIndex })
            }

        }

    }
}
