package com.cornellappdev.android.eatery.ui.components.general

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.cornellappdev.android.eatery.ui.theme.EateryBlue
import com.cornellappdev.android.eatery.util.LocationHandler
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState

/**
 * Creates a Permission request dialog for requesting location permissions.
 */
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PermissionRequestDialog(
    showBottomBar: MutableState<Boolean>,
    notificationFlowStatus: Boolean,
    updateNotificationFlowStatus: (Boolean) -> Unit
) {
    var requestingPermission by remember { mutableStateOf(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) }
    showBottomBar.value = !requestingPermission

    AnimatedVisibility(
        visible = requestingPermission,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        val context = LocalContext.current

        val notificationPermissionState =
            rememberMultiplePermissionsState(
                permissions = listOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            )

        if (notificationPermissionState.allPermissionsGranted) {
            LocationHandler.instantiate(context)
            requestingPermission = false
        } else {
            Surface(
                color = Color.Black.copy(alpha = 0.6f),
                modifier = Modifier.fillMaxSize()
            ) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .padding(horizontal = 20.dp),
                        elevation = 10.dp
                    ) {
                        Column(
                            modifier = Modifier.padding(33.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Location permissions are necessary to show you " +
                                        "eateries that are the closest to you!" +
                                        if (notificationPermissionState.shouldShowRationale || !notificationFlowStatus) {
                                            ""
                                        } else {
                                            "\n\nPlease click the button below to go to the settings to enable notifications."
                                        },
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.height(15.dp))
                            Button(
                                onClick = {
                                    if (notificationPermissionState.shouldShowRationale || !notificationFlowStatus) {
                                        notificationPermissionState.launchMultiplePermissionRequest()
                                        updateNotificationFlowStatus(true)
                                    } else {
                                        context.openSettings()
                                    }
                                    requestingPermission = false
                                },
                                shape = RoundedCornerShape(5.dp),
                                colors = ButtonDefaults.buttonColors(backgroundColor = EateryBlue),
                            ) {
                                Text(
                                    text = if (notificationPermissionState.shouldShowRationale || !notificationFlowStatus) {
                                        "Request Permission"
                                    } else {
                                        "Open Settings"
                                    },
                                    color = Color.White,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

fun Context.openSettings() {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    intent.data = Uri.fromParts("package", packageName, null)
    startActivity(intent)
}
