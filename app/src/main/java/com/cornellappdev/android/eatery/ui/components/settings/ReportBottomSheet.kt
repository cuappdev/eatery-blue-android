package com.cornellappdev.android.eatery.ui.components.settings

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cornellappdev.android.eatery.R
import com.cornellappdev.android.eatery.data.models.NetworkError
import com.cornellappdev.android.eatery.ui.theme.EateryBlueTypography
import com.cornellappdev.android.eatery.ui.theme.currentColors
import com.cornellappdev.android.eatery.ui.viewmodels.state.NetworkAction
import com.cornellappdev.android.eatery.ui.viewmodels.state.NetworkUiError
import com.cornellappdev.android.eatery.ui.viewmodels.state.ReportUiState
import com.cornellappdev.android.eatery.util.DualModePreview
import com.cornellappdev.android.eatery.util.EateryPreview

enum class Issue(@param:StringRes val optionRes: Int) {
    ITEM(R.string.report_issue_item),
    PRICE(R.string.report_issue_price),
    HOURS(R.string.report_issue_hours),
    WAIT_TIMES(R.string.report_issue_wait_times),
    DESCRIPTION(R.string.report_issue_description),
    OTHER(R.string.report_issue_other)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportBottomSheet(
    issue: Issue?,
    eateryId: Int?,
    reportState: ReportUiState,
    sendReport: (issue: String, report: String, eateryId: Int?) -> Unit,
    clearReportState: () -> Unit,
    hide: () -> Unit
) {
    val focusManager = LocalFocusManager.current
    val (textEntry, setTextEntry) = rememberSaveable { mutableStateOf("") }
    val (selectedIssue, setSelectedIssue) = rememberSaveable(issue) { mutableStateOf(issue) }
    var showIssueSheet by remember { mutableStateOf(false) }
    val isSending = reportState is ReportUiState.Sending
    val issueSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val issueEntries = Issue.entries.toTypedArray()
    val selectedIssueLabel =
        stringResource(selectedIssue?.optionRes ?: R.string.report_choose_option)

    LaunchedEffect(reportState) {
        if (reportState is ReportUiState.Success) {
            hide()
            setTextEntry("")
            setSelectedIssue(issue)
            clearReportState()
        }
    }
    if (showIssueSheet) {
        ModalBottomSheet(
            onDismissRequest = { showIssueSheet = false },
            sheetState = issueSheetState,
            shape = RoundedCornerShape(
                bottomStart = 0.dp,
                bottomEnd = 0.dp,
                topStart = 12.dp,
                topEnd = 12.dp
            )
        ) {
            Row(modifier = Modifier.padding(top = 12.dp)) {
                Spacer(Modifier.weight(1f, true))
                HorizontalDivider(
                    Modifier.weight(0.75f, true),
                    color = currentColors.backgroundSecondary,
                    thickness = 3.dp
                )
                Spacer(Modifier.weight(1f, true))
            }
            IssueBottomSheet(issueEntries, {
                setSelectedIssue(it)
                clearReportState()
            }) {
                showIssueSheet = false
            }
        }
    }

    Column {
        Row(modifier = Modifier.statusBarsPadding()) {
            Spacer(Modifier.weight(1f, true))
            HorizontalDivider(
                Modifier.weight(0.75f, true),
                color = currentColors.backgroundSecondary,
                thickness = 3.dp
            )
            Spacer(Modifier.weight(1f, true))
        }

        Column(
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.report_title),
                    style = EateryBlueTypography.h4,
                    color = currentColors.textPrimary,
                )

                IconButton(
                    onClick = {
                        clearReportState()
                        hide()
                    },
                    enabled = !isSending,
                    modifier = Modifier
                        .size(40.dp)
                        .background(color = currentColors.backgroundDefault, shape = CircleShape)
                ) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = Icons.Default.Close.name,
                        tint = currentColors.textPrimary
                    )
                }
            }
            Text(
                text = stringResource(R.string.report_type_heading),
                style = EateryBlueTypography.h5,
                color = currentColors.textPrimary,
                modifier = Modifier.padding(top = 15.dp)
            )
            Button(
                shape = RoundedCornerShape(corner = CornerSize(8.dp)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 5.dp),
                onClick = {
                    focusManager.clearFocus()
                    showIssueSheet = true
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = currentColors.backgroundDefault,
                    contentColor = Color.Black
                )
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = selectedIssueLabel,
                        style = EateryBlueTypography.h6,
                        color = if (selectedIssue == null) currentColors.textSecondary else currentColors.textPrimary
                    )
                    Icon(
                        imageVector = Icons.Default.ExpandMore,
                        contentDescription = null
                    )
                }
            }

            Text(
                text = stringResource(R.string.report_description_heading),
                style = EateryBlueTypography.h5,
                color = currentColors.textPrimary,
                modifier = Modifier.padding(top = 15.dp, bottom = 5.dp)
            )

            val onSubmit = {
                focusManager.clearFocus()
            }

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = currentColors.textPrimary,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp)
                ) {
                    if (textEntry.isEmpty()) {
                        Text(
                            text = stringResource(R.string.report_description_hint),
                            style = EateryBlueTypography.h6,
                            color = Color.Gray
                        )
                    }
                    BasicTextField(
                        modifier = Modifier.fillMaxSize(),
                        value = textEntry,
                        onValueChange = {
                            setTextEntry(it)
                            clearReportState()
                        },
                        keyboardActions = KeyboardActions(onDone = { onSubmit() }),
                        textStyle = TextStyle(
                            color = currentColors.textPrimary,
                            fontWeight = FontWeight.Medium,
                            fontSize = 16.sp
                        ),
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Sentences,
                            autoCorrectEnabled = false,
                        )
                    )
                }
            }

            if (reportState is ReportUiState.Error) {
                Text(
                    text = stringResource(R.string.report_error_unable_to_send),
                    style = EateryBlueTypography.subtitle2,
                    color = Color.Red,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Button(
                shape = RoundedCornerShape(corner = CornerSize(24.dp)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
                    .height(48.dp),
                onClick = {
                    if (isSending || selectedIssue == null) return@Button

                    focusManager.clearFocus()
                    sendReport(selectedIssue.name, textEntry.trim(), eateryId)
                },
                enabled = textEntry.isNotBlank() && selectedIssue != null && !isSending,
                colors = ButtonDefaults.buttonColors(
                    containerColor = currentColors.accentPrimary,
                    contentColor = currentColors.backgroundDefault,
                    disabledContainerColor = currentColors.backgroundSecondary,
                    disabledContentColor = currentColors.backgroundDefault
                )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                )
                {
                    if (!isSending)
                        Text(
                            text = stringResource(R.string.report_submit),
                            style = EateryBlueTypography.h5,
                            color = currentColors.backgroundDefault
                        )
                    else
                        CircularProgressIndicator(
                            color = currentColors.backgroundDefault,
                            modifier = Modifier.size(30.dp)
                        )
                }
            }
        }
    }
}

@Composable
private fun IssueBottomSheet(items: Array<Issue>, setIssue: (Issue) -> Unit, hide: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 8.dp)
    ) {
        items.forEachIndexed { index, issue ->
            SettingsOption(
                title = stringResource(issue.optionRes),
                onClick = {
                    setIssue(issue)
                    hide()
                }
            )
            if (index < items.size - 1)
                SettingsLineSeparator()
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@DualModePreview
@Composable
private fun ReportBottomSheetSuccessPreview() = EateryPreview {
    ReportBottomSheet(
        issue = Issue.ITEM,
        eateryId = null,
        reportState = ReportUiState.Success,
        sendReport = { _, _, _ -> },
        clearReportState = {},
        hide = {}
    )
}

@DualModePreview
@Composable
private fun ReportBottomSheetErrorPreview() = EateryPreview {
    ReportBottomSheet(
        issue = Issue.ITEM,
        eateryId = null,
        reportState = ReportUiState.Error(
            error = NetworkUiError.Failed(NetworkAction.SendReport, NetworkError.NetworkFailure)
        ),
        sendReport = { _, _, _ -> },
        clearReportState = {},
        hide = {}
    )
}
