package com.cornellappdev.android.eatery.ui.components.general

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.cornellappdev.android.eatery.R
import com.cornellappdev.android.eatery.ui.theme.currentColors
import com.cornellappdev.android.eatery.util.DualModePreview
import com.cornellappdev.android.eatery.util.EateryPreview
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
    val context = LocalContext.current
    var isRequestingPermission by remember {
        mutableStateOf(!context.hasLocationPermission())
    }
    showBottomBar.value = !isRequestingPermission

    AnimatedVisibility(
        visible = isRequestingPermission,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        val locationPermissionState =
            rememberMultiplePermissionsState(
                permissions = listOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            )

        if (locationPermissionState.allPermissionsGranted) {
            LocationHandler.instantiate(context)
            isRequestingPermission = false
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.6f)),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(horizontal = 20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(33.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(
                                if (locationPermissionState.shouldShowRationale || !notificationFlowStatus) {
                                    R.string.permission_location_message
                                } else {
                                    R.string.permission_location_message_with_settings
                                }
                            ),
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(15.dp))
                        Button(
                            onClick = {
                                if (locationPermissionState.shouldShowRationale || !notificationFlowStatus) {
                                    locationPermissionState.launchMultiplePermissionRequest()
                                    updateNotificationFlowStatus(true)
                                } else {
                                    context.openSettings()
                                }
                                isRequestingPermission = false
                            },
                            shape = RoundedCornerShape(5.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = currentColors.accentPrimary),
                        ) {
                            Text(
                                text = stringResource(
                                    if (locationPermissionState.shouldShowRationale || !notificationFlowStatus) {
                                        R.string.request_permission
                                    } else {
                                        R.string.open_settings
                                    }
                                ),
                                color = currentColors.textPrimary,
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun Context.hasLocationPermission(): Boolean {
    val coarsePermission = ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.ACCESS_COARSE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED
    val finePermission = ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED
    return coarsePermission && finePermission
}

private fun Context.openSettings() {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    intent.data = Uri.fromParts("package", packageName, null)
    startActivity(intent)
}

@DualModePreview
@Composable
fun PermissionRequestDialogPreview() = EateryPreview {
    PermissionRequestDialog(
        showBottomBar = remember { mutableStateOf(true) },
        notificationFlowStatus = false,
        updateNotificationFlowStatus = {}
    )
}


