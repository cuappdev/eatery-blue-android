package com.cornellappdev.android.eatery.util

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Green
import com.cornellappdev.android.eatery.data.models.Eatery
import com.cornellappdev.android.eatery.data.models.EateryStatus
import com.cornellappdev.android.eatery.data.models.Event
import com.cornellappdev.android.eatery.data.models.MenuCategory
import com.cornellappdev.android.eatery.data.models.MenuItem
import com.cornellappdev.android.eatery.ui.components.general.Filter
import com.cornellappdev.android.eatery.ui.components.general.MenuCategoryViewState
import com.cornellappdev.android.eatery.ui.components.general.MenuItemViewState
import com.cornellappdev.android.eatery.ui.components.upcoming.EateryHours
import com.cornellappdev.android.eatery.ui.components.upcoming.MenuCardViewState
import com.cornellappdev.android.eatery.ui.viewmodels.EateriesSection
import com.cornellappdev.android.eatery.ui.viewmodels.UpcomingMenusViewState
import com.cornellappdev.android.eatery.ui.viewmodels.state.EateryApiResponse
import java.time.LocalDateTime
import kotlin.random.Random

object PreviewData {

    data class CompareMenusPreviewState(
        val eateries: List<Eatery>,
        val events: List<Event?>,
    )

    data class UpcomingMenuPreviewState(
        val viewState: UpcomingMenusViewState,
        val upcomingMenuFilters: List<Filter>,
    )

    private val defaultUpcomingMenuFilters = listOf(
        Filter.FromEateryFilter.North,
        Filter.FromEateryFilter.Central,
        Filter.FromEateryFilter.West
    )

    fun mockEatery(id:Int = Random.nextInt()) =
        Eatery(
            id = id,
            name = "Test Eatery",
            location = "Test Location",
        )

    fun compareMenusPreviewState(): CompareMenusPreviewState {
        val okenshieldsEvent = mockEvent(
            id = 1,
            eateryId = 1,
            items = listOf("Chicken Tikka Masala", "Roasted Vegetables")
        )
        val beckerEvent = mockEvent(
            id = 2,
            eateryId = 2,
            items = listOf("Baked Ziti", "Garden Salad")
        )

        return CompareMenusPreviewState(
            eateries = listOf(
                mockEatery(1).copy(name = "Okenshields", events = listOf(okenshieldsEvent)),
                mockEatery(2).copy(name = "Becker House Dining Room", events = listOf(beckerEvent))
            ),
            events = listOf(okenshieldsEvent, beckerEvent)
        )
    }

    fun upcomingMenuPreviewState(): UpcomingMenuPreviewState {
        return UpcomingMenuPreviewState(
            viewState = UpcomingMenusViewState(
                menus = EateryApiResponse.Success(
                    listOf(
                        EateriesSection(
                            header = "North",
                            menuCards = listOf(
                                MenuCardViewState(
                                    eateryId = 1,
                                    name = "Morrison Dining",
                                    eateryHours = EateryHours(
                                        startTime = "11:00 AM",
                                        endTime = "2:30 PM"
                                    ),
                                    eateryStatus = EateryStatus(
                                        statusText = "Open",
                                        statusColor = Green
                                    ),
                                    menu = listOf(
                                        MenuCategoryViewState(
                                            category = "Lunch",
                                            items = listOf(
                                                MenuItemViewState(
                                                    item = MenuItem(name = "Chicken Tikka Masala"),
                                                    isFavorite = true
                                                ),
                                                MenuItemViewState(
                                                    item = MenuItem(name = "Roasted Vegetables"),
                                                    isFavorite = false
                                                )
                                            )
                                        )
                                    )
                                )
                            )
                        ),
                        EateriesSection(
                            header = "West",
                            menuCards = listOf(
                                MenuCardViewState(
                                    eateryId = 2,
                                    name = "Becker House Dining Room",
                                    eateryHours = EateryHours(
                                        startTime = "5:00 PM",
                                        endTime = "8:00 PM"
                                    ),
                                    eateryStatus = EateryStatus(
                                        statusText = "Closing Soon",
                                        statusColor = Color(0xFFFFA500) // Orange
                                    ),
                                    menu = listOf(
                                        MenuCategoryViewState(
                                            category = "Dinner",
                                            items = listOf(
                                                MenuItemViewState(
                                                    item = MenuItem(name = "Baked Ziti"),
                                                    isFavorite = false
                                                ),
                                                MenuItemViewState(
                                                    item = MenuItem(name = "Garden Salad"),
                                                    isFavorite = true
                                                )
                                            )
                                        )
                                    )
                                )
                            )
                        )
                    )
                )
            ),
            upcomingMenuFilters = defaultUpcomingMenuFilters
        )
    }

    fun upcomingMenuEmptyPreviewState(): UpcomingMenuPreviewState {
        return UpcomingMenuPreviewState(
            viewState = UpcomingMenusViewState(
                menus = EateryApiResponse.Success(emptyList())
            ),
            upcomingMenuFilters = defaultUpcomingMenuFilters
        )
    }

    fun upcomingMenuErrorPreviewState(): UpcomingMenuPreviewState {
        return UpcomingMenuPreviewState(
            viewState = UpcomingMenusViewState(
                menus = EateryApiResponse.Error
            ),
            upcomingMenuFilters = defaultUpcomingMenuFilters
        )
    }

    private fun mockEvent(
        id: Int,
        eateryId: Int,
        items: List<String>,
    ): Event {
        val start = LocalDateTime.now().minusMinutes(30)
        val end = LocalDateTime.now().plusHours(1)
        return Event(
            id = id,
            eateryId = eateryId,
            type = "Lunch",
            startTimestamp = start,
            endTimestamp = end,
            menu = mutableListOf(
                MenuCategory(
                    name = "Entrees",
                    items = items.map { MenuItem(name = it) }
                )
            )
        )
    }

}
