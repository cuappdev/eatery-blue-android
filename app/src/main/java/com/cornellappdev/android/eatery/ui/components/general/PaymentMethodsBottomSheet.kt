package com.cornellappdev.android.eatery.ui.components.general

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cornellappdev.android.eatery.R
import com.cornellappdev.android.eatery.ui.theme.EateryBlueTypography
import com.cornellappdev.android.eatery.ui.theme.currentColors

@Composable
fun PaymentMethodsBottomSheet(
    selectedFilters: MutableList<Filter>,
    hide: () -> Unit
) {
    Column(
        modifier = Modifier
            .padding(start = 16.dp, end = 16.dp, top = 24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(R.string.payment_methods_title),
                style = EateryBlueTypography.h4,
                modifier = Modifier.padding(bottom = 12.dp),
                color = currentColors.textPrimary
            )

            IconButton(
                onClick = {
                    hide()
                },
                modifier = Modifier
                    .size(40.dp)
                    .background(color = currentColors.backgroundDefault, shape = CircleShape)
            ) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = stringResource(R.string.payment_methods_close),
                    tint = currentColors.textPrimary
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, bottom = 16.dp, start = 16.dp, end = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                IconButton(
                    onClick = {
                        if (selectedFilters.contains(Filter.FromEateryFilter.Swipes)) {
                            selectedFilters.remove(Filter.FromEateryFilter.Swipes)
                        } else {
                            selectedFilters.add(Filter.FromEateryFilter.Swipes)
                        }
                    },
                    modifier = Modifier
                        .size(64.dp)
                        .background(
                            color = if (selectedFilters.contains(Filter.FromEateryFilter.Swipes)) {
                                PaymentMethodsAvailable.SWIPES.tintColor
                            } else {
                                currentColors.backgroundDefault
                            },
                            shape = CircleShape
                        )
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_payment_swipes),
                        contentDescription = stringResource(R.string.payment_methods_meal_swipes),
                        tint = if (selectedFilters.contains(Filter.FromEateryFilter.Swipes)) currentColors.backgroundDefault else currentColors.textSecondary
                    )
                }

                Text(
                    text = stringResource(R.string.payment_methods_meal_swipes),
                    style = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 12.sp),
                    modifier = Modifier.padding(top = 8.dp),
                    color = currentColors.textPrimary
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                IconButton(
                    onClick = {
                        if (selectedFilters.contains(Filter.FromEateryFilter.BRB)) {
                            selectedFilters.remove(Filter.FromEateryFilter.BRB)
                        } else {
                            selectedFilters.add(Filter.FromEateryFilter.BRB)
                        }
                    },
                    modifier = Modifier
                        .size(64.dp)
                        .background(
                            color = if (selectedFilters.contains(Filter.FromEateryFilter.BRB)) {
                                PaymentMethodsAvailable.BRB.tintColor
                            } else {
                                currentColors.backgroundDefault
                            },
                            shape = CircleShape
                        )
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_payment_brbs),
                        contentDescription = stringResource(R.string.payment_methods_brbs),
                        tint = if (selectedFilters.contains(Filter.FromEateryFilter.BRB)) currentColors.backgroundDefault else currentColors.textSecondary
                    )
                }

                Text(
                    text = stringResource(R.string.payment_methods_brbs),
                    style = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 12.sp),
                    modifier = Modifier.padding(top = 8.dp),
                    color = currentColors.textPrimary
                )
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                IconButton(
                    onClick = {
                        if (selectedFilters.contains(Filter.FromEateryFilter.Cash)) {
                            selectedFilters.remove(Filter.FromEateryFilter.Cash)
                        } else {
                            selectedFilters.add(Filter.FromEateryFilter.Cash)
                        }
                    },
                    modifier = Modifier
                        .size(64.dp)
                        .background(
                            color = if (selectedFilters.contains(Filter.FromEateryFilter.Cash)) {
                                PaymentMethodsAvailable.CASH.tintColor
                            } else {
                                currentColors.backgroundDefault
                            },
                            shape = CircleShape
                        )
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_payment_cash),
                        contentDescription = stringResource(R.string.payment_methods_cash_or_credit),
                        tint = if (selectedFilters.contains(Filter.FromEateryFilter.Cash)) currentColors.backgroundDefault else currentColors.textSecondary
                    )
                }

                Text(
                    text = stringResource(R.string.payment_methods_cash_or_credit),
                    style = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 12.sp),
                    modifier = Modifier.padding(top = 8.dp),
                    color = currentColors.textPrimary
                )
            }
        }

        Button(
            onClick = { hide() },
            shape = RoundedCornerShape(32.dp),
            modifier = Modifier
                .padding(top = 12.dp)
                .fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = currentColors.accentPrimary,
                contentColor = currentColors.backgroundDefault
            )
        ) {
            Text(
                text = stringResource(R.string.payment_methods_show_results),
                style = EateryBlueTypography.h5,
                modifier = Modifier.padding(top = 8.dp, bottom = 8.dp),
                color = currentColors.textPrimary
            )
        }

        TextButton(
            onClick = {
                selectedFilters.clear()
                hide()
            },
            modifier = Modifier
                .padding(top = 12.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = stringResource(R.string.payment_methods_reset),
                style = EateryBlueTypography.h5,
                color = currentColors.textPrimary
            )
        }
    }
}
