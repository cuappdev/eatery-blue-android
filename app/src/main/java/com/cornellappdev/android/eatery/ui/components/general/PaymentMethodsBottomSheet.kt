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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
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
import com.cornellappdev.android.eatery.util.DualModePreview
import com.cornellappdev.android.eatery.util.EateryPreview

@Composable
fun PaymentMethodsBottomSheet(
    selectedFilters: MutableList<Filter>,
    hide: () -> Unit
) {
    Column(
        modifier = Modifier
            .background(currentColors.backgroundDefault)
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
                    .background(color = currentColors.accentPrimary, shape = CircleShape)
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
            PaymentMethodsAvailable.entries.forEach { paymentMethod ->
                val filters = paymentMethod.filters
                val isSelected = selectedFilters.intersect(filters).isNotEmpty()
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    IconButton(
                        onClick = {
                            if (isSelected) {
                                selectedFilters.removeAll(filters)
                            } else {
                                selectedFilters.addAll(filters)
                            }
                        },
                        modifier = Modifier.size(64.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = paymentMethod.drawable),
                            contentDescription = stringResource(paymentMethod.textRes),
                            tint = if (isSelected) paymentMethod.tintColor else currentColors.textSecondary
                        )
                    }

                    Text(
                        text = stringResource(paymentMethod.textRes),
                        style = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 12.sp),
                        modifier = Modifier.padding(top = 8.dp),
                        color = currentColors.textPrimary
                    )
                }
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

@DualModePreview
@Composable
private fun PaymentMethodsBottomSheetPreview() = EateryPreview {
    val selectedFilters = remember {
        mutableStateListOf<Filter>(Filter.FromEateryFilter.Swipes)
    }
    PaymentMethodsBottomSheet(
        selectedFilters = selectedFilters,
        hide = {}
    )
}
