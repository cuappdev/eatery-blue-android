package com.cornellappdev.android.eatery.ui.components.comparemenus

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cornellappdev.android.eatery.data.models.Eatery
import com.cornellappdev.android.eatery.ui.components.general.Filter
import com.cornellappdev.android.eatery.ui.components.general.FilterRow
import com.cornellappdev.android.eatery.ui.theme.EateryBlue
import com.cornellappdev.android.eatery.ui.theme.EateryBlueTypography
import com.cornellappdev.android.eatery.ui.theme.GrayTwo
import com.cornellappdev.android.eatery.ui.theme.GrayZero
import com.cornellappdev.android.eatery.ui.viewmodels.CompareMenusBottomViewModel
import com.cornellappdev.android.eatery.util.EateryPreview
import com.cornellappdev.android.eatery.util.PreviewData
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun CompareMenusBottomSheet(
    onDismiss: () -> Unit,
    onCompareMenusClick: (selectedEateriesIds: List<Int>) -> Unit,
    compareMenusBottomViewModel: CompareMenusBottomViewModel = hiltViewModel(),
    firstEatery: Eatery? = null
) {
    val compareMenusUIState by compareMenusBottomViewModel.compareMenusUiState.collectAsStateWithLifecycle()

    LaunchedEffect(firstEatery) {
        compareMenusBottomViewModel.initializedFirstEatery(firstEatery)
    }

    CompareMenusBottomSheetContent(
        onDismiss = {
            compareMenusBottomViewModel.resetSelected()
            onDismiss()
        },
        onCompareMenusClick = onCompareMenusClick,
        filters = compareMenusUIState.filters,
        availableFilters = compareMenusBottomViewModel.compareMenusBottomSheetFilters,
        selectedEateries = compareMenusUIState.selected,
        eateries = compareMenusUIState.eateries,
        onFilterClicked = compareMenusBottomViewModel::toggleFilter,
        onToggleEatery = { eatery ->
            if (compareMenusUIState.selected.contains(eatery)) {
                compareMenusBottomViewModel.removeSelected(eatery)
            } else {
                compareMenusBottomViewModel.addSelected(eatery)
            }
        }
    )
}

@Composable
fun CompareMenusBottomSheetContent(
    onDismiss: () -> Unit,
    onCompareMenusClick: (selectedEateriesIds: List<Int>) -> Unit,
    filters: List<Filter>,
    availableFilters: List<Filter>,
    selectedEateries: List<Eatery>,
    eateries: List<Eatery>,
    onFilterClicked: (Filter) -> Unit,
    onToggleEatery: (Eatery) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 14.dp)
            .padding(16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Compare Menus", style = EateryBlueTypography.h4,
                color = Color.Black
            )
            IconButton(
                onClick = onDismiss,
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
    }

    FilterRow(
        currentFiltersSelected = filters,
        onFilterClicked = onFilterClicked,
        filters = availableFilters
    )
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp, 10.dp, 16.dp, 16.dp),
        horizontalAlignment = Alignment.Start
    ) {

        Box(
            modifier = Modifier
                .background(Color.White)
                .fillMaxHeight(0.4f)
                .fillMaxWidth()
        ) {
            SelectableEateriesList(
                eateries = eateries,
                selectedEateries = selectedEateries,
                onToggleEatery = onToggleEatery
            )
        }

        Spacer(modifier = Modifier.height(12.dp))
        val coroutineScope = rememberCoroutineScope()
        Button(
            onClick = {
                if (selectedEateries.size >= 2) {
                    coroutineScope.launch {
                        delay(100)
                        onCompareMenusClick(selectedEateries.mapNotNull { it.id })
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .align(Alignment.CenterHorizontally),
            shape = RoundedCornerShape(100),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (selectedEateries.size < 2) GrayTwo else EateryBlue,
                contentColor = if (selectedEateries.size < 2) GrayZero else Color.White
            )
        ) {
            Text(
                text = if (selectedEateries.size < 2) "Select at least ${2 - selectedEateries.size} more"
                else "Compare ${selectedEateries.size} now",
                style = EateryBlueTypography.h5
            )
        }
    }
}

@Composable
private fun SelectableEateriesList(
    eateries: List<Eatery>,
    selectedEateries: List<Eatery>,
    onToggleEatery: (Eatery) -> Unit,
) {
    LazyColumn {
        items(eateries) { eatery ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onToggleEatery(eatery)
                    },
                verticalAlignment = Alignment.CenterVertically,

                ) {
                IconButton(
                    onClick = {
                        onToggleEatery(eatery)
                    },
                    modifier = Modifier.align(Alignment.CenterVertically)
                ) {
                    if (selectedEateries.contains(eatery)) {
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
                                modifier = Modifier.size(24.dp)
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
                eatery.name?.let {
                    Text(
                        text = it, style = EateryBlueTypography.body1,
                        color = Color.Black
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun CompareMenusBottomSheetContentPreview() = EateryPreview {
    val mockEateries = listOf(
        PreviewData.mockEatery(1).copy(name = "North Star"),
        PreviewData.mockEatery(2).copy(name = "RPCC"),
        PreviewData.mockEatery(3).copy(name = "Okenshields"),
    )

    CompareMenusBottomSheetContent(
        onDismiss = {},
        onCompareMenusClick = {},
        filters = listOf(Filter.Selected),
        availableFilters = listOf(
            Filter.Selected,
            Filter.FromEateryFilter.North,
            Filter.FromEateryFilter.West,
            Filter.FromEateryFilter.Central,
            Filter.FromEateryFilter.Under10,
        ),
        selectedEateries = mockEateries.take(1),
        eateries = mockEateries,
        onFilterClicked = {},
        onToggleEatery = {},
    )
}