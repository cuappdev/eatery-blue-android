package com.cornellappdev.android.eatery.ui.components.details

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.cornellappdev.android.eatery.R
import com.cornellappdev.android.eatery.data.models.Eatery
import com.cornellappdev.android.eatery.ui.theme.currentColors

/**
 * Payment widgets that are displayed at the top right region eatery details screen
 */
@Composable
fun PaymentWidgets(eatery: Eatery, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Surface(
        modifier = modifier.clickable {
            onClick.invoke()
        }, shape = CircleShape, color = currentColors.backgroundDefault
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(ButtonDefaults.IconSpacing)
        ) {
            if (eatery.paymentAcceptsMealSwipes == true) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_payment_swipes),
                    contentDescription = "Accepts Swipes",
                    tint = currentColors.accentPrimary
                )
            }
            if (eatery.paymentAcceptsBrbs == true) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_payment_brbs),
                    contentDescription = "Accepts BRBs",
                    tint = currentColors.error
                )
            }
            if (eatery.paymentAcceptsCash == true) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_payment_cash),
                    contentDescription = "Accepts Cash",
                    tint = currentColors.success
                )
            }
        }
    }
}
