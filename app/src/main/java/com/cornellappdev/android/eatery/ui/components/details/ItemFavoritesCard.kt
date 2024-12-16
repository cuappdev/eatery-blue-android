import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cornellappdev.android.eatery.R
import com.cornellappdev.android.eatery.data.models.EateryStatus
import com.cornellappdev.android.eatery.ui.components.general.FavoriteButton
import com.cornellappdev.android.eatery.ui.theme.EateryBlueTypography
import com.cornellappdev.android.eatery.ui.theme.GrayZero
import com.cornellappdev.android.eatery.ui.theme.Green
import com.cornellappdev.android.eatery.util.EateryPreview

data class ItemFavoritesCardViewState(
    val itemName: String,
    val availability: EateryStatus,
    val mealAvailability: Map<String, List<String>>
)

@Composable
fun ItemFavoritesCard(
    viewState: ItemFavoritesCardViewState,
    onFavoriteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }
    val rotation: Float by animateFloatAsState(
        if (isExpanded) 180F else 0F,
        label = "chevron rotation"
    )
    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(BorderStroke(Dp.Hairline, GrayZero), RoundedCornerShape(8)),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth()
                .animateContentSize()
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(viewState.itemName, fontSize = 20.sp, style = EateryBlueTypography.button)
                FavoriteButton(isFavorite = true, onFavoriteClick = { onFavoriteClick() })
            }
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    viewState.availability.statusText,
                    fontSize = 12.sp,
                    color = viewState.availability.statusColor,
                    style = EateryBlueTypography.button
                )
                if (viewState.mealAvailability.isNotEmpty()) {
                    Icon(
                        imageVector = ImageVector.vectorResource(id = R.drawable.ic_down_chevron),
                        contentDescription = "expand",
                        modifier = Modifier
                            .clickable(onClick = { isExpanded = !isExpanded })
                            .rotate(rotation)
                    )
                }

            }
            if (isExpanded) {
                Divider(thickness = Dp.Hairline)
                viewState.mealAvailability.forEach { availability ->
                    ItemInformation(availability.key, availability.value)
                }
            }
        }
    }
}

@Composable
fun ItemInformation(meal: String, eateryName: List<String>) {
    Column(
        modifier = Modifier.padding(top = 8.dp)
    ) {
        Text(meal, fontSize = 20.sp, style = EateryBlueTypography.button)
        eateryName.forEach { eatery ->
            Text(eatery, style = EateryBlueTypography.caption, color = Color(0xff7D8288))
        }
    }

}

@Preview
@Composable
private fun FavoritesCardPreview() = EateryPreview {
    ItemFavoritesCard(
        ItemFavoritesCardViewState(
            "tes",
            EateryStatus("Available", Green),
            mapOf(
                "lunch" to listOf("becker"),
                "lunch" to listOf("becker"),
                "lunch" to listOf("becker"),
                "lunch" to listOf("becker")
            ),
        ),
        onFavoriteClick = {}
    )
}