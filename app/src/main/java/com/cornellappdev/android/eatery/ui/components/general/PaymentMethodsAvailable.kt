package com.cornellappdev.android.eatery.ui.components.general

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cornellappdev.android.eatery.R
import com.cornellappdev.android.eatery.data.models.Eatery
import com.cornellappdev.android.eatery.ui.theme.EateryBlueTypography
import com.cornellappdev.android.eatery.ui.theme.currentColors
import com.cornellappdev.android.eatery.util.DualModePreview
import com.cornellappdev.android.eatery.util.EateryPreview


@Composable
fun PaymentMethodsAvailable(
    selectedPaymentMethods: List<PaymentMethodsAvailable>,
    hide: () -> Unit,
) {
    val paymentMethodsAvailableText = buildAnnotatedString {
        append(stringResource(R.string.payment_methods_pay_with))
        append(" ")
        if (selectedPaymentMethods.containsAll(PaymentMethodsAvailable.entries)) {
            append(stringResource(R.string.payment_methods_all))
        } else {
            selectedPaymentMethods.forEachIndexed { index, paymentMethod ->
                appendInlineContent(id = paymentMethod.name)
                pushStyle(SpanStyle(color = paymentMethod.tintColor))
                append(" ${stringResource(paymentMethod.textRes)}")
                pop()
                if (index != selectedPaymentMethods.lastIndex) {
                    append(" ${stringResource(R.string.payment_methods_or)} ")
                }
            }
        }
        append(".")
    }

    val inlineContentMap =
        PaymentMethodsAvailable.entries.associateBy(
            keySelector = { it.name },
            valueTransform = { paymentMethod ->
                InlineTextContent(
                    Placeholder(18.sp, 18.sp, PlaceholderVerticalAlign.TextCenter)
                ) {
                    Image(
                        painter = painterResource(id = paymentMethod.drawable),
                        modifier = Modifier
                            .fillMaxSize()
                            .background(color = Color.Transparent),
                        contentDescription = paymentMethod.name
                    )
                }
            }
        )

    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 24.dp)
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
                .padding(top = 12.dp, bottom = 24.dp, start = 16.dp, end = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            PaymentMethodsAvailable.entries.forEach { paymentMethod ->
                PaymentMethodIcon(
                    paymentMethod = paymentMethod,
                    isSelected = selectedPaymentMethods.contains(paymentMethod)
                )
            }
        }

        Text(
            text = paymentMethodsAvailableText,
            style = TextStyle(fontSize = 18.sp),
            color = currentColors.textPrimary,
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
                containerColor = currentColors.backgroundDefault92,
                contentColor = currentColors.textPrimary
            )
        ) {
            Text(
                text = stringResource(R.string.payment_methods_close),
                style = EateryBlueTypography.h5,
                color = currentColors.textPrimary,
                modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
            )
        }
    }
}

@Composable
private fun PaymentMethodIcon(
    paymentMethod: PaymentMethodsAvailable,
    isSelected: Boolean
) {
    Surface(
        modifier = Modifier.size(64.dp),
        color = currentColors.backgroundDefault
    ) {
        Icon(
            painter = painterResource(id = paymentMethod.drawable),
            contentDescription = stringResource(paymentMethod.textRes),
            tint = if (isSelected) {
                paymentMethod.tintColor
            } else {
                currentColors.textSecondary
            }
        )
    }
}

enum class PaymentMethodsAvailable(val drawable: Int, val textRes: Int) {
    SWIPES(
        drawable = R.drawable.ic_payment_swipes,
        textRes = R.string.payment_methods_meal_swipes
    ),
    BRB(
        drawable = R.drawable.ic_payment_brbs,
        textRes = R.string.payment_methods_brbs
    ),
    CASH_OR_CARD(
        drawable = R.drawable.ic_payment_cash,
        textRes = R.string.payment_methods_cash_or_credit
    );
}

val PaymentMethodsAvailable.tintColor: Color
    @Composable
    get() = when (this) {
        PaymentMethodsAvailable.SWIPES -> currentColors.contentBrand
        PaymentMethodsAvailable.BRB -> currentColors.error
        PaymentMethodsAvailable.CASH_OR_CARD -> currentColors.success
    }

fun PaymentMethodsAvailable.isAcceptedBy(eatery: Eatery): Boolean = when (this) {
    PaymentMethodsAvailable.SWIPES -> eatery.acceptsMealSwipes()
    PaymentMethodsAvailable.BRB -> eatery.acceptsBRB()
    PaymentMethodsAvailable.CASH_OR_CARD -> eatery.acceptsCash() || eatery.acceptsCard()
}

val PaymentMethodsAvailable.filters: Set<Filter.FromEateryFilter>
    get() = when (this) {
        PaymentMethodsAvailable.SWIPES -> setOf(Filter.FromEateryFilter.Swipes)
        PaymentMethodsAvailable.BRB -> setOf(Filter.FromEateryFilter.BRB)
        PaymentMethodsAvailable.CASH_OR_CARD -> setOf(
            Filter.FromEateryFilter.Cash,
            Filter.FromEateryFilter.Card
        )
    }

@DualModePreview
@Composable
private fun PaymentMethodsAvailableAllPreview() {
    EateryPreview {
        PaymentMethodsAvailable(
            selectedPaymentMethods = PaymentMethodsAvailable.entries,
            hide = {}
        )
    }
}

@DualModePreview
@Composable
private fun PaymentMethodsAvailablePartialPreview() {
    EateryPreview {
        PaymentMethodsAvailable(
            selectedPaymentMethods = listOf(
                PaymentMethodsAvailable.BRB,
                PaymentMethodsAvailable.CASH_OR_CARD
            ),
            hide = {}
        )
    }
}

