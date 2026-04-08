package com.cornellappdev.android.eatery.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Report
import androidx.compose.material.icons.outlined.ArrowOutward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cornellappdev.android.eatery.R
import com.cornellappdev.android.eatery.ui.components.settings.Issue
import com.cornellappdev.android.eatery.ui.components.settings.ReportBottomSheet
import com.cornellappdev.android.eatery.ui.components.settings.SettingsLineSeparator
import com.cornellappdev.android.eatery.ui.components.settings.SettingsOption
import com.cornellappdev.android.eatery.ui.theme.EateryBlue
import com.cornellappdev.android.eatery.ui.theme.EateryBlueTypography
import com.cornellappdev.android.eatery.ui.theme.GrayFive
import com.cornellappdev.android.eatery.ui.theme.GraySix
import com.cornellappdev.android.eatery.ui.theme.GrayZero
import com.cornellappdev.android.eatery.ui.viewmodels.SupportViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupportScreen(supportViewModel: SupportViewModel = hiltViewModel()) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val reportState by supportViewModel.reportState.collectAsStateWithLifecycle()
    val modalBottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
    )
    var showReportSheet by remember { mutableStateOf(false) }
    var issue by remember { mutableStateOf<Issue?>(null) }

    if (showReportSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                supportViewModel.clearReportState()
                showReportSheet = false
            },
            sheetState = modalBottomSheetState,
            shape = RoundedCornerShape(
                bottomStart = 0.dp,
                bottomEnd = 0.dp,
                topStart = 12.dp,
                topEnd = 12.dp
            )
        ) {
            ReportBottomSheet(
                issue = issue,
                eateryId = null,
                reportState = reportState,
                sendReport = { issue, report, _ ->
                    supportViewModel.sendReport(issue, report)
                },
                clearReportState = supportViewModel::clearReportState,
            ) {
                coroutineScope.launch {
                    modalBottomSheetState.hide()
                }.invokeOnCompletion {
                    if (!modalBottomSheetState.isVisible) showReportSheet = false
                }
            }
        }
    }

            Column(
                modifier = Modifier
                    .padding(start = 16.dp, end = 16.dp)
                    .fillMaxSize()
                    .then(Modifier.statusBarsPadding())
            ) {
                Text(
                    text = "Support",
                    color = EateryBlue,
                    style = EateryBlueTypography.h2,
                    modifier = Modifier.padding(top = 7.dp)
                )
                Text(
                    text = "Report issues and contact Cornell AppDev",
                    style = TextStyle(fontWeight = FontWeight.Medium, fontSize = 18.sp),
                    color = GraySix,
                    modifier = Modifier.padding(top = 7.dp, bottom = 24.dp)
                )

                Text(
                    text = "Make Eatery Better",
                    color = Color.Black,
                    style = EateryBlueTypography.h4,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                Text(
                    text = "Help us improve Eatery by letting us know what’s wrong.",
                    color = GrayFive,
                    style = EateryBlueTypography.subtitle2,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                Button(
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier
                        .height(48.dp)
                        .fillMaxWidth(),
                    onClick = {
                        issue = null
                        showReportSheet = true
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = EateryBlue,
                        contentColor = Color.White
                    )
                ) {
                    Icon(imageVector = Icons.Default.Report, Icons.Default.Report.name)
                    Text(
                        text = "Report an Issue",
                        style = EateryBlueTypography.h5,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }

                TextButton(modifier = Modifier.align(Alignment.CenterHorizontally), onClick = {
                    val email = Intent(Intent.ACTION_SENDTO)
                    email.data =
                        "mailto:team@cornellappdev.com?subject=${Uri.encode("Eatery - Reporting an Issue")}".toUri()
                    context.startActivity(Intent.createChooser(email, "Choose an Email client :"))
                }) {
                    Text(
                        text = "Shoot us an email",
                        style = EateryBlueTypography.button,
                        color = EateryBlue
                    )
                    Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                    Icon(
                        Icons.Outlined.ArrowOutward,
                        null,
                        tint = EateryBlue
                    )
                }

                Text(
                    text = "Frequently Asked Questions",
                    style = EateryBlueTypography.h4,
                    color = Color.Black,
                    modifier = Modifier.padding(top = 20.dp)
                )
                FAQCreation(
                    title = "Why do I see wrong or empty menus?",
                    dropdownText = stringResource(id = R.string.wrong_empty_menus),
                    action = {
                        ReportButton()
                    }
                ) {
                    issue = Issue.ITEM
                    showReportSheet = true
                }

                FAQCreation(
                    title = "Why is an eatery closed when it says it should be open?",
                    dropdownText = stringResource(id = R.string.eatery_closed_when_open),
                    action = {
                        ReportButton()
                    }
                ) {
                    issue = Issue.HOURS
                    showReportSheet = true
                }

                FAQCreation(
                    title = "Why are the wait times longer?",
                    dropdownText = stringResource(id = R.string.wait_time_longer),
                    action = {
                        ReportButton()
                    }
                ) {
                    issue = Issue.WAIT_TIMES
                    showReportSheet = true
                }

                FAQCreation(
                    title = "Why can’t I order food on Eatery?",
                    dropdownText = stringResource(id = R.string.order_on_eatery),
                    action = {
                        Row {
                            Text(
                                text = "send them an email.",
                                style = EateryBlueTypography.subtitle2,
                                color = EateryBlue,
                            )
                        }
                    }
                ) {
                    val email = Intent(Intent.ACTION_SENDTO)
                    email.data =
                        "mailto:dining@cornell.edu?subject=${Uri.encode("Ordering Food on Eatery")}".toUri()

                    context.startActivity(Intent.createChooser(email, "Choose an Email client :"))
                }
            }
}

@Composable
private fun ReportButton() {
    Surface(
        shape = RoundedCornerShape(17.dp),
        modifier = Modifier
            .height(50.dp)
            .padding(vertical = 8.dp),
        color = GrayZero,
        contentColor = Color.Black
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(ButtonDefaults.ContentPadding)
        ) {
            Icon(imageVector = Icons.Default.Report, Icons.Default.Report.name)
            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
            Text(
                text = "Report an Issue",
                style = EateryBlueTypography.button,
                color = Color.Black,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FAQCreation(
    title: String,
    dropdownText: String,
    action: @Composable () -> Unit,
    onActionClick: () -> Unit
) {
    val (expanded, setExpanded) = remember {
        mutableStateOf(false)
    }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = setExpanded) {
        SettingsOption(
            title = title,
            trailingIcon = {
                if (expanded) {
                    Icon(
                        imageVector = Icons.Default.ExpandLess,
                        contentDescription = Icons.Default.ExpandLess.name,
                        tint = EateryBlue,
                        modifier = Modifier.width(24.dp)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.ExpandMore,
                        contentDescription = null,
                        tint = EateryBlue,
                        modifier = Modifier.width(24.dp)
                    )
                }
            },
            onClick = { setExpanded(!expanded) }
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { setExpanded(false) },
            modifier = Modifier
                .exposedDropdownSize()
        ) {
            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = dropdownText,
                    style = EateryBlueTypography.subtitle2,
                    color = GrayFive,
                )
                Box(modifier = Modifier.clickable {
                    setExpanded(false)
                    onActionClick()
                }) {
                    action()
                }
            }
        }
    }
    SettingsLineSeparator()
}
