package com.cornellappdev.android.eateryblue.ui.components.general

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cornellappdev.android.eateryblue.R
import com.cornellappdev.android.eateryblue.ui.theme.EateryBlue
import com.cornellappdev.android.eateryblue.ui.theme.EateryBlueTypography
import com.cornellappdev.android.eateryblue.ui.theme.GrayFive
import com.cornellappdev.android.eateryblue.ui.theme.GrayZero
import com.cornellappdev.android.eateryblue.ui.theme.Green
import com.cornellappdev.android.eateryblue.ui.theme.Red

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
                text = "Payment Methods",
                style = EateryBlueTypography.h4,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            IconButton(
                onClick = {
                    hide()
                },
                modifier = Modifier
                    .size(40.dp)
                    .background(color = GrayZero, shape = CircleShape)
            ) {
                Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.Black)
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
                        if (selectedFilters.contains(Filter.SWIPES)) {
                            selectedFilters.remove(Filter.SWIPES)
                        } else {
                            selectedFilters.add(Filter.SWIPES)
                        }
                    },
                    modifier = Modifier
                        .size(64.dp)
                        .background(
                            color = if (selectedFilters.contains(Filter.SWIPES)) EateryBlue else GrayZero,
                            shape = CircleShape
                        )
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_payment_swipes),
                        contentDescription = "Swipes",
                        tint = if (selectedFilters.contains(Filter.SWIPES)) Color.White else GrayFive
                    )
                }

                Text(
                    text = "Meal Swipes",
                    style = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 12.sp),
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                IconButton(
                    onClick = {
                        if (selectedFilters.contains(Filter.BRB)) {
                            selectedFilters.remove(Filter.BRB)
                        } else {
                            selectedFilters.add(Filter.BRB)
                        }
                    },
                    modifier = Modifier
                        .size(64.dp)
                        .background(
                            color = if (selectedFilters.contains(Filter.BRB)) Red else GrayZero,
                            shape = CircleShape
                        )
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_payment_brbs),
                        contentDescription = "BRBs",
                        tint = if (selectedFilters.contains(Filter.BRB)) Color.White else GrayFive
                    )
                }

                Text(
                    text = "BRBs",
                    style = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 12.sp),
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                IconButton(
                    onClick = {
                        if (selectedFilters.contains(Filter.CASH)) {
                            selectedFilters.remove(Filter.CASH)
                        } else {
                            selectedFilters.add(Filter.CASH)
                        }
                    },
                    modifier = Modifier
                        .size(64.dp)
                        .background(
                            color = if (selectedFilters.contains(Filter.CASH)) Green else GrayZero,
                            shape = CircleShape
                        )
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_payment_cash),
                        contentDescription = "Cash or credit",
                        tint = if (selectedFilters.contains(Filter.CASH)) Color.White else GrayFive
                    )
                }

                Text(
                    text = "Cash or credit",
                    style = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 12.sp),
                    modifier = Modifier.padding(top = 8.dp)
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
                backgroundColor = EateryBlue,
                contentColor = Color.White
            )
        ) {
            Text(
                text = "Show results",
                style = EateryBlueTypography.h5,
                modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
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
                text = "Reset",
                style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.SemiBold),
                color = Color.Black
            )
        }
    }
}
