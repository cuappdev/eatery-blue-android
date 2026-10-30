package com.cornellappdev.android.eatery.ui.components.details

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.cornellappdev.android.eatery.data.models.Eatery
import com.cornellappdev.android.eatery.data.models.PaymentMethod
import com.cornellappdev.android.eatery.ui.components.general.PaymentMethodsAvailable
import com.cornellappdev.android.eatery.ui.components.general.isAcceptedBy
import com.cornellappdev.android.eatery.ui.components.general.tintColor
import com.cornellappdev.android.eatery.ui.theme.currentColors
import com.cornellappdev.android.eatery.util.DualModePreview
import com.cornellappdev.android.eatery.util.EateryPreview
import com.cornellappdev.android.eatery.util.PreviewData

/**
 * Payment widgets that are displayed at the top right region eatery details screen
 */
@Composable
fun PaymentWidgets(eatery: Eatery, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Surface(
        modifier = modifier.clickable {
            onClick.invoke()
        },
        shape = CircleShape,
        color = currentColors.accentPrimary
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(ButtonDefaults.IconSpacing)
        ) {
            PaymentMethodsAvailable.entries.forEach { paymentMethod ->
                if (paymentMethod.isAcceptedBy(eatery)) {
                    Icon(
                        painter = painterResource(id = paymentMethod.drawable),
                        contentDescription = "Accepts ${paymentMethod.name}",
                        tint = paymentMethod.tintColor
                    )
                }
            }
        }
    }
}

@DualModePreview
@Composable
private fun PaymentWidgetsPreview() = EateryPreview {
    PaymentWidgets(
        eatery = PreviewData.mockEatery().copy(
            paymentMethods = listOf(
                PaymentMethod.MEAL_SWIPE,
                PaymentMethod.BRB,
                PaymentMethod.CASH
            )
        ),
        onClick = {}
    )
}
