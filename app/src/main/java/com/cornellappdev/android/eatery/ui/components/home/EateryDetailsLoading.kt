package com.cornellappdev.android.eatery.ui.components.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cornellappdev.android.eatery.ui.theme.GrayTwo
import com.valentinilk.shimmer.Shimmer
import com.valentinilk.shimmer.ShimmerBounds
import com.valentinilk.shimmer.rememberShimmer
import com.valentinilk.shimmer.shimmer

@Composable
fun EateryDetailLoadingScreen(
    shimmer: Shimmer
) {
    Column {
        Box {

            // this is where the image should be
            Box(modifier = Modifier.background(color = Color.White)) {
                Surface(
                    modifier = Modifier
                        .height(240.dp)
                        .shimmer(shimmer)
                        .fillMaxWidth(), color = GrayTwo
                ) {}
            }

            Surface(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 40.dp, end = 16.dp)
                    .size(40.dp), color = Color.White,
                shape = CircleShape
            ) {}

            Spacer(modifier = Modifier.height(200.dp))

            Row(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
            ) {
                PaymentsBlob()
            }
        }

        // simulates eatery name and details
        Column(modifier = Modifier.background(Color.White)) {
            Surface(
                modifier = Modifier
                    .padding(start = 16.dp, top = 16.dp)
                    .shimmer(shimmer)
                    .height(40.dp)
                    .fillMaxWidth(.6f), color = GrayTwo,
                shape = RoundedCornerShape(20.dp)
            ) {}
            Surface(
                modifier = Modifier
                    .padding(start = 16.dp, end = 16.dp, top = 10.dp)
                    .fillMaxWidth()
                    .shimmer(shimmer)
                    .height(20.dp),
                shape = RoundedCornerShape(20.dp),
                color = GrayTwo
            ) {}

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp)
                    .align(Alignment.Start),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                repeat(2) {
                    Surface(
                        modifier = Modifier
                            .height(50.dp)
                            .shimmer(shimmer)
                            .padding(horizontal = 16.dp)
                            .weight(0.3f),
                        shape = RoundedCornerShape(100),
                        color = GrayTwo
                    ) {
                    }
                }
            }

            Surface(
                modifier = Modifier
                    .padding(top = 10.dp)
                    .shimmer(shimmer)
                    .fillMaxHeight(.2f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                shape = RoundedCornerShape(8.dp), color = GrayTwo
            ) {}

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(16.dp)
                    .shimmer(shimmer), color = GrayTwo
            ) {}

            // simulates menu dropdown
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
            ) {
                Surface(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .shimmer(shimmer)
                        .padding(16.dp)
                        .size(35.dp), color = GrayTwo,
                    shape = CircleShape
                ) {}

                Box {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight()
                    ) {
                        Surface(
                            modifier = Modifier
                                .fillMaxHeight(.2f)
                                .fillMaxWidth(.6f)
                                .shimmer(shimmer)
                                .padding(16.dp),
                            color = GrayTwo,
                            shape = RoundedCornerShape(100.dp)
                        ) {}

                        Surface(
                            modifier = Modifier
                                .padding()
                                .fillMaxHeight(.25f)
                                .fillMaxWidth()
                                .shimmer(shimmer)
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            shape = RoundedCornerShape(8.dp), color = GrayTwo
                        ) {}

                        Spacer(modifier = Modifier.height(8.dp))

                        Surface(
                            modifier = Modifier
                                .height(3.dp)
                                .fillMaxWidth()
                                .shimmer(shimmer)
                                .padding(horizontal = 16.dp), color = GrayTwo
                        ) {}

                        Spacer(modifier = Modifier.height(8.dp))

                        Surface(
                            modifier = Modifier
                                .fillMaxHeight(.2f)
                                .fillMaxWidth(.6f)
                                .shimmer(shimmer)
                                .padding(horizontal = 16.dp, vertical = 5.dp),
                            color = GrayTwo,
                            shape = RoundedCornerShape(100.dp)
                        ) {}

                        Spacer(modifier = Modifier.height(8.dp))

                        Row {
                            Surface(
                                modifier = Modifier
                                    .padding()
                                    .fillMaxHeight(.2f)
                                    .shimmer(shimmer)
                                    .fillMaxWidth(.45f)
                                    .padding(horizontal = 16.dp, vertical = 3.dp),
                                shape = RoundedCornerShape(8.dp), color = GrayTwo
                            ) {}

                            Spacer(modifier = Modifier.weight(1f))
                            Surface(
                                modifier = Modifier
                                    .padding()
                                    .fillMaxHeight(.2f)
                                    .shimmer(shimmer)
                                    .fillMaxWidth(.35f)
                                    .padding(horizontal = 16.dp, vertical = 3.dp),
                                shape = RoundedCornerShape(8.dp), color = GrayTwo
                            ) {}
                        }

                        Spacer(modifier = Modifier.fillMaxHeight(.1f))

                        Surface(
                            modifier = Modifier
                                .padding()
                                .fillMaxHeight(.2f)
                                .shimmer(shimmer)
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            shape = RoundedCornerShape(100.dp), color = GrayTwo
                        ) {}
                    }
                }
            }
        }
    }
}


@Composable
fun PaymentsBlob() {
    Surface(
        Modifier
            .fillMaxHeight(.05f)
            .fillMaxWidth(.25f),
        shape = RoundedCornerShape(100.dp),
        color = Color.White
    ) {
    }
}


@Preview(showBackground = true)
@Composable
fun show() {
    val shimmer = rememberShimmer(ShimmerBounds.View)
    EateryDetailLoadingScreen(shimmer)
}

/**
 * Details all the possible bottom sheets for EateryDetailScreen.
 *
 * All possible bottom sheets should be added here and switched to before expanding via modalBottomSheetState.
 */
enum class BottomSheetContent {
    PAYMENT_METHODS_AVAILABLE, HOURS, WAIT_TIME, REPORT, ACCOUNT_TYPE, MENUS
}
