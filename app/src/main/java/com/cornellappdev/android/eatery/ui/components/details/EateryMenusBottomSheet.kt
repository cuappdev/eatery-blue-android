package com.cornellappdev.android.eatery.ui.components.details

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cornellappdev.android.eatery.data.models.Eatery
import com.cornellappdev.android.eatery.data.models.Event
import com.cornellappdev.android.eatery.data.models.MealTime
import com.cornellappdev.android.eatery.ui.components.general.CalendarWeekSelector
import com.cornellappdev.android.eatery.ui.theme.EateryBlue
import com.cornellappdev.android.eatery.ui.theme.EateryBlueTypography
import com.cornellappdev.android.eatery.ui.theme.GrayZero
import com.cornellappdev.android.eatery.util.EateryPreview
import com.cornellappdev.android.eatery.util.PreviewData
import com.cornellappdev.android.eatery.util.toMealTypeDisplayName
import com.cornellappdev.android.eatery.util.toReadableShortName
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
    onResetClick: () -> Unit
) {
    val zoneId: ZoneId? = ZoneId.of("America/New_York")
    val today = LocalDate.now(zoneId)
    val currentDay by remember { mutableStateOf(today) }

    val weekDates = (0..6).map { currentDay.plusDays(it.toLong()) }
    val dayWeeks = weekDates.map { it.dayOfWeek.value }
    val days = weekDates.map { it.dayOfMonth }
    val dayNames = weekDates.map { it.dayOfWeek.toReadableShortName() }

    var selectedDay by remember { mutableStateOf(weekDayIndex) }
    var currSelectedDay by remember { mutableStateOf(selectedDay) }

    val closedDays: List<DayOfWeek> = eatery.getClosedDays()
    val closedDaysStrings: List<String> = closedDays.map { dayOfWeek ->
        dayOfWeek.toReadableShortName()
    }

    val selectedDayOfWeek = DayOfWeek.of(dayWeeks[currSelectedDay])
    val mealTypes: List<MealTime> = eatery.getTypeMeal(selectedDayOfWeek)
    var selectedMealType by remember {
        mutableStateOf(mealTypes.getOrNull(mealType)?.mealType ?: "")
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
                    selectedMealType = mealTypes.firstOrNull()?.mealType ?: ""
                },
                modifier = Modifier.padding(bottom = 12.dp),
                closedDays = closedDaysStrings,
                eateryDetail = true
            )

            //display of possible meal descriptions (none for cafés)
            Column(
                modifier = Modifier
                    .padding(top = 12.dp, bottom = 12.dp)
                    .fillMaxWidth()
            ) {
                if (mealTypes.size > 1) {
                    mealTypes.forEachIndexed { index, (description, duration) ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = description.toMealTypeDisplayName(),
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight(600),
                                    color = Color.Black,
                                    modifier = Modifier.padding(bottom = 2.dp)
                                )
                                Text(
                                    text = duration,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight(600),
                                    color = Color.Gray
                                )
                            }
                            IconButton(onClick = { selectedMealType = description }) {
                                if (selectedMealType == description) {
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier
                                            .size(26.dp)
                                            .background(Color.Black, CircleShape)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Selected",
                                            tint = Color.White,
                                            modifier = Modifier.fillMaxSize(0.7f)
                                        )
                                    }
                                } else {
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier
                                            .size(26.dp)
                                            .background(Color.White, CircleShape)
                                            .border(2.dp, Color.Black, CircleShape)
                                    ) {
                                    }
                                }
                            }
                        }
                        if (mealTypes.lastIndex != index) {
                            Spacer(
                                modifier = Modifier
                                    .padding(top = 12.dp, bottom = 12.dp)
                                    .fillMaxWidth()
                                    .height(1.dp)
                                    .background(GrayZero, CircleShape)
                            )
                        }
                    }
                }
            }

            // Show menu and reset menu buttons
            Column(
                modifier = Modifier.padding(bottom = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = {
                        selectedDay = currSelectedDay
                        onShowMenuClick(
                            currSelectedDay,
                            selectedMealType,
                            mealTypes.indexOfFirst { it.mealType == selectedMealType })
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
                ClickableText(
                    modifier = Modifier.padding(top = 12.dp),
                    text = AnnotatedString("Reset"),
                    style = TextStyle(
                        fontSize = 14.sp,
                        lineHeight = 17.5.sp,
                        fontWeight = FontWeight(600),
                        color = Color(0xFF050505)
                    ),
                    onClick = {
                        selectedDay = weekDayIndex
                        onResetClick()
                        onDismiss()
                    })
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun EateryMenusBottomSheetPreview() = EateryPreview {
    val zoneId = ZoneId.of("America/New_York")
    val today = LocalDate.now(zoneId)
    val previewEatery = PreviewData.mockEatery().copy(
        events = listOf(
            Event(
                type = "Breakfast",
                startTimestamp = today.atTime(8, 0),
                endTimestamp = today.atTime(10, 0)
            ),
            Event(
                type = "Lunch",
                startTimestamp = today.atTime(11, 0),
                endTimestamp = today.atTime(14, 0)
            ),
            Event(
                type = "Dinner",
                startTimestamp = today.atTime(17, 0),
                endTimestamp = today.atTime(20, 0)
            ),
            Event(
                type = "Lunch",
                startTimestamp = today.plusDays(1).atTime(11, 0),
                endTimestamp = today.plusDays(1).atTime(14, 0)
            )
        )
    )

    EateryMenusBottomSheet(
        weekDayIndex = 0,
        mealType = 1,
        onDismiss = {},
        eatery = previewEatery,
        onShowMenuClick = { _, _, _ -> },
        onResetClick = {}
    )
}
