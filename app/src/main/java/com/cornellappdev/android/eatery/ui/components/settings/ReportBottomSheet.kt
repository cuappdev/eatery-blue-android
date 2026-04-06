package com.cornellappdev.android.eatery.ui.components.settings

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cornellappdev.android.eatery.ui.theme.EateryBlue
import com.cornellappdev.android.eatery.ui.theme.EateryBlueTypography
import com.cornellappdev.android.eatery.ui.theme.GrayFive
import com.cornellappdev.android.eatery.ui.theme.GrayOne
import com.cornellappdev.android.eatery.ui.theme.GrayZero
import com.cornellappdev.android.eatery.ui.theme.Red
import com.cornellappdev.android.eatery.util.EateryPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class Issue(val option: String) {
    ITEM("Inaccurate or missing item"),
    PRICE("Different price than listed"),
    HOURS("Incorrect hours"),
    WAIT_TIMES("Inaccurate wait times"),
    DESCRIPTION("Inaccurate description"),
    OTHER("Other")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportBottomSheet(
    issue: Issue?,
    eateryId: Int?,
    sendReport: suspend (issue: String, report: String, eateryId: Int?) -> Boolean,
    hide: () -> Unit
) {
    val focusManager = LocalFocusManager.current
    val coroutineScope = rememberCoroutineScope()
    val (textEntry, setTextEntry) = rememberSaveable { mutableStateOf("") }
    val (selectedIssue, setSelectedIssue) = rememberSaveable(issue) { mutableStateOf(issue) }
    var showIssueSheet by remember { mutableStateOf(false) }
    var isSending by remember { mutableStateOf(false) }
    var submissionError by rememberSaveable { mutableStateOf<String?>(null) }
    val issueSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val issueEntries = Issue.entries.toTypedArray()
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
                HorizontalDivider(Modifier.weight(0.75f, true), color = GrayOne, thickness = 3.dp)
                Spacer(Modifier.weight(1f, true))
            }
            IssueBottomSheet(issueEntries, {
                setSelectedIssue(it)
                submissionError = null
            }) {
                showIssueSheet = false
            }
        }
    }

    Column {
        Row(modifier = Modifier.statusBarsPadding()) {
            Spacer(Modifier.weight(1f, true))
            HorizontalDivider(Modifier.weight(0.75f, true), color = GrayOne, thickness = 3.dp)
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
                    text = "Report an Issue",
                    style = EateryBlueTypography.h4,
                    color = Color.Black,
                )

                IconButton(
                    onClick = hide,
                    enabled = !isSending,
                    modifier = Modifier
                        .size(40.dp)
                        .background(color = GrayZero, shape = CircleShape)
                ) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = Icons.Default.Close.name,
                        tint = Color.Black
                    )
                }
            }
            Text(
                text = "Type of issue",
                style = EateryBlueTypography.h5,
                color = Color.Black,
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
                    containerColor = GrayZero,
                    contentColor = Color.Black
                )
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = selectedIssue?.option ?: "Choose an option...",
                        style = EateryBlueTypography.h6,
                        color = if (selectedIssue == null) GrayFive else Color.Black
                    )
                    Icon(
                        imageVector = Icons.Default.ExpandMore,
                        contentDescription = ""
                    )
                }
            }

            Text(
                text = "Description",
                style = EateryBlueTypography.h5,
                color = Color.Black,
                modifier = Modifier.padding(top = 15.dp, bottom = 5.dp)
            )

            val onSubmit = {
                focusManager.clearFocus()
            }

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = GrayZero,
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
                            text = "Tell us what's wrong...",
                            style = EateryBlueTypography.h6,
                            color = GrayFive
                        )
                    }
                    BasicTextField(
                        modifier = Modifier.fillMaxSize(),
                        value = textEntry,
                        onValueChange = {
                            setTextEntry(it)
                            submissionError = null
                        },
                        keyboardActions = KeyboardActions(onDone = { onSubmit() }),
                        textStyle = TextStyle(
                            color = Color.Black,
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

            submissionError?.let {
                Text(
                    text = it,
                    style = EateryBlueTypography.subtitle2,
                    color = Red,
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
                    isSending = true
                    submissionError = null

                    coroutineScope.launch {
                        val report = textEntry.trim()
                        val success = sendReport(selectedIssue.option, report, eateryId)
                        if (success) {
                            hide()
                            setTextEntry("")
                            setSelectedIssue(issue)
                        } else {
                            submissionError = "Unable to send report. Please try again."
                        }
                        isSending = false
                    }
                },
                enabled = textEntry.isNotBlank() && selectedIssue != null && !isSending,
                colors = ButtonDefaults.buttonColors(
                    containerColor = EateryBlue,
                    contentColor = Color.White,
                    disabledContainerColor = GrayOne,
                    disabledContentColor = Color.White
                )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                )
                {
                    if (!isSending)
                        Text(
                            text = "Submit",
                            style = EateryBlueTypography.h5,
                            color = Color.White
                        )
                    else
                        CircularProgressIndicator(
                            color = Color.White,
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
                title = issue.option,
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

@Preview
@Composable
private fun ReportBottomSheetSuccessPreview() = EateryPreview {
    ReportBottomSheet(
        issue = Issue.ITEM,
        eateryId = null,
        sendReport = { _, _, _ -> delay(3000); true },
        hide = {}
    )
}

@Preview
@Composable
private fun ReportBottomSheetErrorPreview() = EateryPreview {
    ReportBottomSheet(
        issue = Issue.ITEM,
        eateryId = null,
        sendReport = { _, _, _ -> delay(3000); false },
        hide = {}
    )
}