package com.cornellappdev.android.eateryblue.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cornellappdev.android.eateryblue.R
import com.cornellappdev.android.eateryblue.data.models.Eatery
import com.cornellappdev.android.eateryblue.data.models.Event
import com.cornellappdev.android.eateryblue.ui.components.general.PaymentMethodsAvailable
import com.cornellappdev.android.eateryblue.ui.theme.*
import com.cornellappdev.android.eateryblue.ui.viewmodels.EateryDetailViewModel
import com.cornellappdev.android.eateryblue.ui.viewmodels.state.EateryRetrievalState
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.components.rememberImageComponent
import com.skydoves.landscapist.glide.GlideImage
import com.skydoves.landscapist.placeholder.shimmer.ShimmerPlugin
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun EateryDetailScreen(
    eateryDetailViewModel: EateryDetailViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val modalBottomSheetState =
        rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
    var sheetContent by remember { mutableStateOf(BottomSheetContent.PAYMENT_METHODS_AVAILABLE) }
    val paymentMethods = remember { mutableStateListOf<PaymentMethodsAvailable>() }
    val coroutineScope = rememberCoroutineScope()

    ModalBottomSheetLayout(
        sheetState = modalBottomSheetState,
        sheetContent = {
            // Supports multiple different Bottom Sheets. Additional Bottom Sheets
            // should be added as a branch here and as an Enum class under BottomSheetContent.
            when (sheetContent) {
                BottomSheetContent.PAYMENT_METHODS_AVAILABLE -> {
                    PaymentMethodsAvailable(selectedPaymentMethods = paymentMethods) {
                        coroutineScope.launch {
                            modalBottomSheetState.hide()
                        }
                    }
                }
                BottomSheetContent.HOURS -> {
                    // TODO finish
                }
                BottomSheetContent.WAIT_TIME -> {
                    // TODO finish
                }
            }
        },
        sheetShape = RoundedCornerShape(
            bottomStart = 0.dp,
            bottomEnd = 0.dp,
            topStart = 12.dp,
            topEnd = 12.dp
        ),
        sheetElevation = 8.dp
    ) {
        when (eateryDetailViewModel.eateryRetrievalState) {
            is EateryRetrievalState.Pending -> {

            }
            is EateryRetrievalState.Error -> {

            }
            is EateryRetrievalState.Success -> {
                val eatery = eateryDetailViewModel.eatery
                paymentMethods.apply {
                    if (eatery.paymentAcceptsCash == true) add(PaymentMethodsAvailable.CASH)
                    if (eatery.paymentAcceptsBrbs == true) add(PaymentMethodsAvailable.BRB)
                    if (eatery.paymentAcceptsMealSwipes == true) add(PaymentMethodsAvailable.SWIPES)
                }
                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                ) {
                    Box {
                        Box {
                            GlideImage(
                                imageModel = { eatery.imageUrl ?: "" },
                                modifier = Modifier
                                    .height(240.dp)
                                    .fillMaxWidth(),
                                imageOptions = ImageOptions(
                                    contentScale = ContentScale.Crop,
                                ),
                                component = rememberImageComponent {
                                    +ShimmerPlugin(
                                        baseColor = Color.White,
                                        highlightColor = GrayZero,
                                        durationMillis = 350,
                                        dropOff = 0.65f,
                                        tilt = 20f
                                    )
                                },
                                failure = {
                                    Image(
                                        modifier = Modifier
                                            .height(240.dp)
                                            .fillMaxWidth(),
                                        painter = painterResource(R.drawable.blank_eatery),
                                        contentDescription = "Eatery Image",
                                        contentScale = ContentScale.Crop,
                                    )
                                }
                            )
                            if (eatery.isClosed()) {
                                Spacer(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(240.dp)
                                        .background(color = Color.White.copy(alpha = 0.53f))
                                )
                            }
                        }

                        Button(
                            onClick = { eateryDetailViewModel.toggleFavorite() },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(top = 40.dp, end = 16.dp)
                                .size(40.dp),
                            contentPadding = PaddingValues(6.dp),
                            shape = CircleShape,
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = Color.White,
                            )
                        ) {
                            Icon(
                                imageVector = if (eateryDetailViewModel.isFavorite) Icons.Filled.Star else Icons.Outlined.StarOutline,
                                tint = if (eateryDetailViewModel.isFavorite) Yellow else GrayFive,
                                contentDescription = null
                            )
                        }

                        PaymentsWidget(
                            eatery,
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(16.dp)
                                .height(40.dp)
                        ) {
                            sheetContent = BottomSheetContent.PAYMENT_METHODS_AVAILABLE
                            coroutineScope.launch {
                                modalBottomSheetState.show()
                            }
                        }
                    }

                    Column(modifier = Modifier.background(Color.White)) {
                        Text(
                            text = eatery.name ?: "Loading...",
                            modifier = Modifier.padding(start = 16.dp, top = 16.dp),
                            style = EateryBlueTypography.h3,
                        )
                        Text(
                            text = "${eatery.location} ${if (!eatery.menuSummary.isNullOrBlank()) "· ${eatery.menuSummary}" else ""}",
                            modifier = Modifier.padding(start = 16.dp),
                            style = EateryBlueTypography.subtitle2,
                            color = GrayFive
                        )
                        Row(
                            modifier = Modifier
                                .padding(vertical = 12.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Button(
                                onClick = {
                                    val getAppIntent =
                                        context.packageManager.getLaunchIntentForPackage("com.cbord.get")
                                    if (getAppIntent != null) {
                                        getAppIntent.addCategory(Intent.CATEGORY_LAUNCHER)
                                        context.startActivity(getAppIntent)
                                    } else {
                                        val openPlayIntent = Intent(Intent.ACTION_VIEW).apply {
                                            data = Uri.parse(
                                                "https://play.google.com/store/apps/details?id=com.cbord.get"
                                            )
                                            setPackage("com.android.vending")
                                        }
                                        context.startActivity(openPlayIntent)
                                    }
                                },
                                shape = RoundedCornerShape(100),
                                colors = ButtonDefaults.buttonColors(
                                    backgroundColor = EateryBlue,
                                    contentColor = Color.White
                                )
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_android_phone),
                                    contentDescription = "Phone - Order Online"
                                )
                                Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                                Text(
                                    modifier = Modifier.padding(vertical = 6.dp),
                                    text = "Order online",
                                    style = EateryBlueTypography.h5,
                                    color = Color.White
                                )
                            }
                            Button(
                                onClick = {
                                    val mapIntent = Intent(Intent.ACTION_VIEW).apply {
                                        data =
                                            Uri.parse("google.navigation:q=${eatery.latitude},${eatery.longitude}&mode=w")
                                        setPackage("com.google.android.apps.maps")
                                    }
                                    context.startActivity(mapIntent)
                                },
                                shape = RoundedCornerShape(100),
                                colors = ButtonDefaults.buttonColors(
                                    backgroundColor = GrayZero,
                                    contentColor = Color.Black
                                )
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_walk),
                                    contentDescription = "Walk - Get Directions"
                                )
                                Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                                Text(
                                    modifier = Modifier.padding(vertical = 6.dp),
                                    text = "Get directions",
                                    style = EateryBlueTypography.h5,
                                    maxLines = 1
                                )
                            }
                        }

                        //AlertsSection(eatery = eatery)

                        Row(
                            modifier = Modifier
                                .height(IntrinsicSize.Min)
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp)
                                .border(
                                    1.dp,
                                    GrayZero,
                                    RoundedCornerShape(8.dp)
                                ),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .padding(vertical = 12.dp)
                                    .weight(1f, true)
                            ) {
                                Row(
                                    modifier = Modifier.clickable {
                                        sheetContent = BottomSheetContent.HOURS
                                        coroutineScope.launch {
                                            modalBottomSheetState.show()
                                        }
                                    },
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Schedule,
                                        contentDescription = "Hours Icon",
                                        tint = GrayFive
                                    )
                                    Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                                    Text(
                                        text = "Hours",
                                        style = TextStyle(
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 16.sp
                                        ),
                                        color = GrayFive
                                    )
                                }
                                val openUntil = eatery.getOpenUntil()
                                Text(
                                    modifier = Modifier.padding(top = 2.dp),
                                    text = openUntil ?: "Closed",
                                    style = TextStyle(
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 16.sp
                                    ),
                                    color = if (openUntil.isNullOrBlank()) Red else Green,
                                )
                            }

                            Divider(
                                color = GrayZero,
                                modifier = Modifier
                                    .align(Alignment.CenterVertically)
                                    .fillMaxHeight(0.5f)
                                    .width(1.dp)
                            )

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .padding(vertical = 12.dp)
                                    .weight(1f, true)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.clickable {
                                        sheetContent = BottomSheetContent.WAIT_TIME
                                        coroutineScope.launch {
                                            modalBottomSheetState.show()
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.HourglassTop,
                                        contentDescription = "Watch Icon",
                                        tint = GrayFive
                                    )
                                    Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                                    Text(
                                        text = "Wait Time",
                                        style = TextStyle(
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 16.sp
                                        ),
                                        color = GrayFive
                                    )
                                }
                                /*
                                val waitTimes = eatery.getWaitTimes()
                                Text(
                                    modifier = Modifier.padding(top = 2.dp),
                                    text = if (!waitTimes.isNullOrEmpty() && !eatery.isClosed()) {
                                        "$waitTimes minutes"
                                    } else {
                                        "-"
                                    },
                                    style = TextStyle(
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 16.sp
                                    ),
                                    color = Color.Black,
                                )

                                 */
                            }
                        }

                        Spacer(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(16.dp)
                                .background(GrayZero)
                        )

                        eatery.getTodaysEvents().forEach { event ->
                            EateryMenuWidget(event = event)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PaymentsWidget(eatery: Eatery, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Surface(
        modifier = modifier.clickable {
            onClick.invoke()
        },
        shape = CircleShape,
        color = Color.White
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(ButtonDefaults.IconSpacing)
        ) {
            if (eatery.paymentAcceptsMealSwipes == true) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_payment_swipes),
                    contentDescription = "Accepts Swipes",
                    tint = EateryBlue
                )
            }
            if (eatery.paymentAcceptsBrbs == true) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_payment_brbs),
                    contentDescription = "Accepts BRBs",
                    tint = Red
                )
            }
            if (eatery.paymentAcceptsCash == true) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_payment_cash),
                    contentDescription = "Accepts Cash",
                    tint = Green
                )
            }
        }
    }
}

/**
@Composable
fun AlertsSection(eatery: Eatery) {
Column(
verticalArrangement = Arrangement.spacedBy(12.dp),
modifier = Modifier.padding(top = 12.dp)
) {

eatery.alerts?.forEach {
if (!it.description.isNullOrBlank()
&& it.startTime?.isBefore(LocalDateTime.now()) == true
&& it.endTime?.isAfter(LocalDateTime.now()) == true
)
Surface(
modifier = Modifier
.padding(horizontal = 16.dp)
.wrapContentSize(),
shape = RoundedCornerShape(5.dp)
) {
Row(
modifier = Modifier
.fillMaxWidth()
.background(LightBlue)
) {
Icon(
Icons.Default.Info,
contentDescription = "Warning",
tint = EateryBlue
)
Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
Text(
text = it.description,
style = EateryBlueTypography.body2,
color = EateryBlue,
modifier = Modifier.padding(start = 5.dp)
)
}
}
}
}
}
 */

@Composable
fun EateryMenuWidget(event: Event) {
    var openDropdown by remember { mutableStateOf(true) }
    var filterText by remember { mutableStateOf("") }

    Row(
        modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f),
        ) {
            Text(
                text = event.description ?: "Full Menu",
                style = EateryBlueTypography.h4,
            )
            if (event.startTime != null && event.endTime != null) {
                Text(
                    text = "${event.startTime.format(DateTimeFormatter.ofPattern("K:mm a"))} - ${
                        event.endTime.format(
                            DateTimeFormatter.ofPattern("K:mm a")
                        )
                    }",
                    style = EateryBlueTypography.subtitle2,
                    color = GrayFive
                )
            }
        }
        IconButton(
            onClick = {
                openDropdown = !openDropdown
            },
            modifier = Modifier
                .size(28.dp)
                .background(color = GrayZero, shape = CircleShape)
        ) {
            Icon(
                imageVector = if (openDropdown) Icons.Default.ExpandMore else Icons.Default.ExpandLess,
                contentDescription = "Expand menu",
                tint = Color.Black
            )
        }
    }
    /**
    if (openDropdown) {
    Column(modifier = Modifier.padding(vertical = 12.dp)) {
    SearchBar(
    searchText = filterText,
    onSearchTextChange = { filterText = it },
    placeholderText = "Search the menu...",
    modifier = Modifier.padding(horizontal = 16.dp),
    onCancelClicked = {
    filterText = ""
    }
    )

    Spacer(
    modifier = Modifier
    .padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 8.dp)
    .fillMaxWidth()
    .height(1.dp)
    .background(GrayZero, CircleShape)
    )

    event.menu?.forEach { category ->
    val filteredItems =
    category.items?.filter {
    it.name?.contains(filterText, true) ?: it.description?.contains(
    filterText,
    true
    ) ?: false
    }
    if (filteredItems.isNullOrEmpty())
    return@forEach

    Text(
    text = category.category ?: "Category",
    style = EateryBlueTypography.h5,
    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
    )
    filteredItems.forEachIndexed { index, menuItem ->
    Column(
    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
    Row {
    Text(
    text = menuItem.name ?: "Item Name",
    style = EateryBlueTypography.button,
    modifier = Modifier.weight(1f)
    )
    if (menuItem.basePrice != null) {
    Text(
    text = String.format("$%.2f", menuItem.basePrice),
    style = EateryBlueTypography.subtitle2,
    color = GrayFive
    )
    }

    }
    if (!menuItem.description.isNullOrBlank()) {
    Text(
    text = menuItem.description,
    style = EateryBlueTypography.body2,
    modifier = Modifier.weight(1f),
    color = GrayFive
    )
    }
    }
    if (category.items.lastIndex != index) {
    Spacer(
    modifier = Modifier
    .padding(horizontal = 16.dp)
    .fillMaxWidth()
    .height(1.dp)
    .background(GrayZero, CircleShape)
    )
    }
    }
    Spacer(
    modifier = Modifier
    .fillMaxWidth()
    .height(8.dp)
    .background(GrayZero)
    )
    }
    }

    Spacer(modifier = Modifier.height(20.dp))
    }
     */
}

/**
 * Details all the possible bottom sheets for EateryDetailScreen.
 *
 * All possible bottom sheets should be added here and switched to before expanding via modalBottomSheetState.
 */
enum class BottomSheetContent {
    PAYMENT_METHODS_AVAILABLE, HOURS, WAIT_TIME
}
