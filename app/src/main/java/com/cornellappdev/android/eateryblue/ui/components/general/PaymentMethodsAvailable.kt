package com.cornellappdev.android.eateryblue.ui.components.general

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cornellappdev.android.eateryblue.R
import com.cornellappdev.android.eateryblue.ui.theme.*


@Composable
fun PaymentMethodsAvailable(
    selectedPaymentMethods: List<PaymentMethodsAvailable>,
    hide: () -> Unit
) {
    val paymentMethodsAvailableText = buildAnnotatedString {
        append("Pay with ")
        if (selectedPaymentMethods.containsAll(PaymentMethodsAvailable.values().toList())) {
            append("all payment methods")
        } else {
            selectedPaymentMethods.forEachIndexed { index, paymentMethod ->
                appendInlineContent(id = paymentMethod.name)
                pushStyle(SpanStyle(color = paymentMethod.color))
                append(" ${paymentMethod.text}")
                pop()
                if (index != selectedPaymentMethods.lastIndex) {
                    append(" or ")
                }
            }
        }
        append(".")
        toAnnotatedString()
    }

    val inlineContentMap =
        PaymentMethodsAvailable.values().associate { paymentMethod ->
            paymentMethod.name to InlineTextContent(
                Placeholder(18.sp, 18.sp, PlaceholderVerticalAlign.TextCenter)
            ) {
                Image(
                    painter = painterResource(id = paymentMethod.drawable),
                    modifier = Modifier.fillMaxSize(),
                    contentDescription = paymentMethod.name
                )
            }
        }

    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 24.dp)
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
                .padding(top = 12.dp, bottom = 24.dp, start = 16.dp, end = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    modifier = Modifier
                        .size(64.dp)
                        .background(
                            color = Color.White,
                            shape = CircleShape
                        )
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_payment_swipes),
                        contentDescription = "Swipes",
                        tint = if (selectedPaymentMethods.contains(PaymentMethodsAvailable.SWIPES)) EateryBlue else GrayTwo
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    modifier = Modifier
                        .size(64.dp)
                        .background(
                            color = Color.White,
                            shape = CircleShape
                        )
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_payment_brbs),
                        contentDescription = "BRBs",
                        tint = if (selectedPaymentMethods.contains(PaymentMethodsAvailable.BRB)) Red else GrayTwo
                    )
                }
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    modifier = Modifier
                        .size(64.dp)
                        .background(
                            color = Color.White,
                            shape = CircleShape
                        )
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_payment_cash),
                        contentDescription = "Cash or card",
                        tint = if (selectedPaymentMethods.contains(PaymentMethodsAvailable.CASH)) Green else GrayTwo
                    )
                }
            }
        }

        Text(
            text = paymentMethodsAvailableText,
            style = TextStyle(fontSize = 18.sp),
            inlineContent = inlineContentMap,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 12.dp)
        )

        Button(
            onClick = { hide() },
            shape = RoundedCornerShape(32.dp),
            modifier = Modifier
                .padding(top = 12.dp, bottom = 12.dp)
                .fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = GrayZero,
                contentColor = Color.Black
            )
        ) {
            Text(
                text = "Close",
                style = EateryBlueTypography.h5,
                modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
            )
        }
    }
}

enum class PaymentMethodsAvailable(val drawable: Int, val color: Color, val text: String) {
    BRB(drawable = R.drawable.ic_small_brbs, color = Red, text = "BRBs"),
    CASH(drawable = R.drawable.ic_small_cash, color = Green, text = "Cash or credit"),
    SWIPES(drawable = R.drawable.ic_small_swipes, color = EateryBlue, text = "Meal swipes");
}
