package com.cornellappdev.android.eatery.ui.components.comparemenus

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.cornellappdev.android.eatery.data.models.Eatery
import com.cornellappdev.android.eatery.ui.components.general.FilterRow
import com.cornellappdev.android.eatery.ui.components.home.MainLoadingItem
import com.cornellappdev.android.eatery.ui.theme.EateryBlue
import com.cornellappdev.android.eatery.ui.theme.EateryBlueTypography
import com.cornellappdev.android.eatery.ui.theme.GrayZero
import com.cornellappdev.android.eatery.ui.viewmodels.HomeViewModel
import com.cornellappdev.android.eatery.ui.viewmodels.state.EateryApiResponse
import kotlinx.coroutines.launch

@Composable
fun CompareMenusBotSheet(
    onDismiss: () -> Unit,
    homeViewModel: HomeViewModel,
    onCompareMenusClick: () -> Unit
) {
    val filters = homeViewModel.CMFiltersFlow.collectAsState().value
    val eateriesApiResponse = homeViewModel.eateryFlow.collectAsState().value

    val selectedEateries = remember {
        mutableStateOf(setOf<Eatery>())
    }

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
            Text(
                text = "Compare Menus", style = EateryBlueTypography.h4,
                color = Color.Black
            )
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

        FilterRow(
            currentFiltersSelected = filters,
            onPaymentMethodsClicked = {},
            onFilterClicked = { filter ->
                if (filters.contains(filter)) {
                    homeViewModel.removeFilterCM(filter)
                } else {
                    homeViewModel.addFilterCM(filter)
                }
            })

        Spacer(modifier = Modifier.height(12.dp))

        when (eateriesApiResponse) {
            is EateryApiResponse.Pending -> {

            }

            is EateryApiResponse.Error -> {
                // TODO Add No Internet/Oopsie display
            }

            is EateryApiResponse.Success -> {
                val eateries = eateriesApiResponse.data
                Box(
                    modifier = Modifier
                        .fillMaxHeight(0.4f)
                        .fillMaxWidth()
                ) {
                    LazyColumn {
                        items(eateries) { eatery ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(
                                    onClick = {
                                        val currentSet = selectedEateries.value.toMutableSet()
                                        if (currentSet.contains(eatery)) {
                                            currentSet.remove(eatery)
                                        } else {
                                            currentSet.add(eatery)
                                        }
                                        selectedEateries.value = currentSet
                                    },
                                    modifier = Modifier.align(Alignment.CenterVertically)
                                ) {
                                    if (selectedEateries.value.contains(eatery)) {
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
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = {
                onCompareMenusClick()
            },
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .height(48.dp)
                .align(Alignment.CenterHorizontally),
            shape = RoundedCornerShape(100),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = EateryBlue, contentColor = Color.White
            )
        ) {
            Text(
                text = if (selectedEateries.value.size < 2) "Select at least ${2 - selectedEateries.value.size} more"
                        else "Compare ${selectedEateries.value.size} now",
                style = EateryBlueTypography.h5,
                color = Color.White
            )
        }
    }
}