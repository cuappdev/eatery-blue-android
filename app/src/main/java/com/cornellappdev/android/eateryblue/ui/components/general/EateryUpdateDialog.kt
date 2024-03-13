package com.cornellappdev.android.eateryblue.ui.components.general

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cornellappdev.android.eateryblue.R
import com.cornellappdev.android.eateryblue.ui.theme.EateryBlue
import com.cornellappdev.android.eateryblue.ui.theme.GrayTwo

/**
 * A dialog for the old eatery blue play store solution.
 * Tells users to switch to the eatery update.
 */
@Preview
@Composable
fun EateryUpdateDialog() {
    var showDialog by remember { mutableStateOf(true) }

    AnimatedVisibility(
        visible = showDialog,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        val context = LocalContext.current

        Surface(
            color = Color.Black.copy(alpha = 0.4f),
            modifier = Modifier
                .fillMaxSize()
                .clickable {
                    showDialog = false
                }) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(horizontal = 20.dp),
                    elevation = 10.dp,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(26.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Text(
                                text = "AppDev Notice",
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.Bold,
                                fontSize = 26.sp,
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Icon(
                            painter = painterResource(id = R.drawable.ic_eaterylogo_blue),
                            contentDescription = null,
                            modifier = Modifier
                                .size(96.dp)
                                .padding(bottom = 8.dp),
                            tint = EateryBlue
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Eatery Blue is now being maintained on the Eatery app as an update.\n\nTo continue using the latest version of Eatery:",
                            textAlign = TextAlign.Center,
                            fontSize = 14.sp,
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Please switch to the Eatery app!",
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 18.sp,
                        )
                        Spacer(modifier = Modifier.height(15.dp))
                        Button(
                            onClick = {
                                val packageName =
                                    "com.cornellappdev.android.eatery" // Replace with your app's package name
                                try {
                                    context.startActivity(
                                        Intent(
                                            Intent.ACTION_VIEW,
                                            Uri.parse("market://details?id=$packageName")
                                        )
                                    )
                                } catch (e: ActivityNotFoundException) {
                                    // If Google Play Store app is not available, open the Play Store website
                                    context.startActivity(
                                        Intent(
                                            Intent.ACTION_VIEW,
                                            Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
                                        )
                                    )
                                }
                            },
                            shape = RoundedCornerShape(99.dp),
                            colors = ButtonDefaults.buttonColors(backgroundColor = EateryBlue),
                        ) {
                            Text(
                                text = "Go to Play Store",
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Icon(
                            painter = painterResource(id = R.drawable.ic_appdev),
                            contentDescription = null,
                            modifier = Modifier
                                .padding(top = 16.dp)
                                .size(32.dp),
                            tint = GrayTwo
                        )
                    }
                }
            }
        }
    }
}
