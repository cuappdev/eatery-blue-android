package com.cornellappdev.android.eatery.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
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
import com.cornellappdev.android.eatery.ui.theme.EateryBlueTypography
import com.cornellappdev.android.eatery.ui.theme.currentColors
import com.cornellappdev.android.eatery.ui.viewmodels.SupportViewModel
import com.cornellappdev.android.eatery.ui.viewmodels.state.ReportUiState
import com.cornellappdev.android.eatery.util.DualModePreview
import com.cornellappdev.android.eatery.util.EateryPreview
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupportScreen(supportViewModel: SupportViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val supportRecipient = stringResource(R.string.support_team_email_recipient)
    val orderFoodRecipient = stringResource(R.string.support_order_food_email_recipient)
    val mailToTemplate = stringResource(R.string.support_mailto_template)
    val reportingIssueSubject = stringResource(R.string.support_reporting_issue_subject)
    val orderFoodSubject = stringResource(R.string.support_order_food_subject)
    val emailChooserTitle = stringResource(R.string.support_email_chooser_title)
    val reportState by supportViewModel.reportState.collectAsStateWithLifecycle()

    SupportScreenContent(
        reportState = reportState,
        clearReportState = supportViewModel::clearReportState,
        sendReport = supportViewModel::sendReport,
        onSendSupportEmail = {
            val email = createMailToIntent(
                recipient = supportRecipient,
                subject = reportingIssueSubject,
                mailToTemplate = mailToTemplate
            )
            context.startActivity(Intent.createChooser(email, emailChooserTitle))
        },
        onSendOrderFoodEmail = {
            val email = createMailToIntent(
                recipient = orderFoodRecipient,
                subject = orderFoodSubject,
                mailToTemplate = mailToTemplate
            )
            context.startActivity(Intent.createChooser(email, emailChooserTitle))
        }
    )
}

private fun createMailToIntent(recipient: String, subject: String, mailToTemplate: String): Intent =
    Intent(Intent.ACTION_SENDTO).apply {
        data = mailToTemplate.format(recipient, Uri.encode(subject)).toUri()
    }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SupportScreenContent(
    reportState: ReportUiState,
    clearReportState: () -> Unit,
    sendReport: (String, String) -> Unit,
    onSendSupportEmail: () -> Unit,
    onSendOrderFoodEmail: () -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    val modalBottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
    )
    var showReportSheet by remember { mutableStateOf(false) }
    var issue by remember { mutableStateOf<Issue?>(null) }
    if (showReportSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                clearReportState()
                showReportSheet = false
            },
            sheetState = modalBottomSheetState,
            containerColor = currentColors.backgroundDefault,
            contentColor = currentColors.textPrimary,
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
                    sendReport(issue, report)
                },
                clearReportState = clearReportState,
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
            .background(color = currentColors.backgroundDefault)
            .padding(horizontal = 16.dp)
            .then(Modifier.statusBarsPadding())
            .fillMaxSize()
    ) {
        Spacer(modifier = Modifier.height(7.dp))
        Text(
            text = stringResource(R.string.support_title),
            color = currentColors.textPrimary,
            style = EateryBlueTypography.h2,
        )
        Spacer(modifier = Modifier.height(7.dp))
        Text(
            text = stringResource(R.string.support_description),
            style = TextStyle(fontWeight = FontWeight.Medium, fontSize = 18.sp),
            color = currentColors.textPrimary,
        )
        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = stringResource(R.string.support_make_eatery_better),
            color = currentColors.textPrimary,
            style = EateryBlueTypography.h4,
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = stringResource(R.string.support_make_eatery_better_description),
            color = currentColors.textSecondary,
            style = EateryBlueTypography.subtitle2,
        )
        Spacer(modifier = Modifier.height(12.dp))
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
                containerColor = currentColors.accentPrimary,
                contentColor = currentColors.backgroundDefault
            )
        ) {
            Icon(imageVector = Icons.Default.Report, Icons.Default.Report.name)
            Text(
                text = stringResource(R.string.report_an_issue),
                style = EateryBlueTypography.h5,
                modifier = Modifier.padding(start = 8.dp),
                color = currentColors.textPrimary
            )
        }

        TextButton(modifier = Modifier.align(Alignment.CenterHorizontally), onClick = {
            onSendSupportEmail()
        }) {
            Text(
                text = stringResource(R.string.support_email_us),
                style = EateryBlueTypography.button,
                color = currentColors.textPrimary
            )
            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
            Icon(
                Icons.Outlined.ArrowOutward,
                null,
                tint = currentColors.backgroundSecondary
            )
        }

        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = stringResource(R.string.support_faqs_heading),
            style = EateryBlueTypography.h4,
            color = currentColors.textPrimary,
        )
        FAQCreation(
            title = stringResource(R.string.support_faq_wrong_menus_title),
            dropdownText = stringResource(id = R.string.wrong_empty_menus),
            action = {
                ReportButton()
            }
        ) {
            issue = Issue.ITEM
            showReportSheet = true
        }

        FAQCreation(
            title = stringResource(R.string.support_faq_closed_hours_title),
            dropdownText = stringResource(id = R.string.eatery_closed_when_open),
            action = {
                ReportButton()
            }
        ) {
            issue = Issue.HOURS
            showReportSheet = true
        }

        FAQCreation(
            title = stringResource(R.string.support_faq_wait_times_title),
            dropdownText = stringResource(id = R.string.wait_time_longer),
            action = {
                ReportButton()
            }
        ) {
            issue = Issue.WAIT_TIMES
            showReportSheet = true
        }

        FAQCreation(
            title = stringResource(R.string.support_faq_order_title),
            dropdownText = stringResource(id = R.string.order_on_eatery),
            action = {
                Row {
                    Text(
                        text = stringResource(R.string.support_faq_order_email_prompt),
                        style = EateryBlueTypography.subtitle2,
                        color = currentColors.textPrimary,
                    )
                }
            }
        ) {
            onSendOrderFoodEmail()
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
        color = currentColors.backgroundDefault,
        contentColor = currentColors.textPrimary
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(ButtonDefaults.ContentPadding)
        ) {
            Icon(imageVector = Icons.Default.Report, Icons.Default.Report.name)
            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
            Text(
                text = stringResource(R.string.report_an_issue),
                style = EateryBlueTypography.button,
                color = currentColors.textPrimary,
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
                        tint = currentColors.backgroundSecondary,
                        modifier = Modifier.width(24.dp)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.ExpandMore,
                        contentDescription = null,
                        tint = currentColors.backgroundSecondary,
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
                    color = currentColors.textSecondary,
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

@DualModePreview
@Composable
private fun SupportScreenPreview() = EateryPreview {
    SupportScreenContent(
        reportState = ReportUiState.Idle,
        clearReportState = {},
        sendReport = { _, _ -> },
        onSendSupportEmail = {},
        onSendOrderFoodEmail = {}
    )
}

