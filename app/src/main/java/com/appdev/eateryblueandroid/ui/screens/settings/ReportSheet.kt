package com.appdev.eateryblueandroid.ui.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.appdev.eateryblueandroid.R
import com.appdev.eateryblueandroid.ui.components.core.CircularBackgroundIcon
import com.appdev.eateryblueandroid.ui.components.core.Text
import com.appdev.eateryblueandroid.ui.components.core.TextField
import com.appdev.eateryblueandroid.ui.components.core.TextStyle
import com.appdev.eateryblueandroid.ui.components.settings.SettingsOption
import com.appdev.eateryblueandroid.ui.screens.SettingsLineSeparator
import com.appdev.eateryblueandroid.ui.viewmodels.BottomSheetViewModel

enum class Issue {
    ITEM, PRICE, HOURS, WAIT_TIMES, DESCRIPTION, OTHER, NONE
}

private val issueMap = hashMapOf(
    Issue.ITEM to "Inaccurate or missing item",
    Issue.PRICE to "Different price than listed",
    Issue.HOURS to "Incorrect hours",
    Issue.WAIT_TIMES to "Inaccurate wait times",
    Issue.DESCRIPTION to "Inaccurate description",
    Issue.OTHER to "Other",
    Issue.NONE to "Choose an option..."
)

data class IssueItem(val issue: Issue, val onClick: () -> Unit)

@Composable
fun ReportSheet(
    issue: Issue,
    bottomSheetViewModel: BottomSheetViewModel,
    issueViewModel: BottomSheetViewModel
) {
    val focusManager = LocalFocusManager.current
    var textEntry by remember { mutableStateOf("") }
    var selectedIssue by remember { mutableStateOf(issue) }
    Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp)) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Report an Issue",
                textStyle = TextStyle.HEADER_H3,
                color = colorResource(id = R.color.black)
            )
            CircularBackgroundIcon(
                icon = painterResource(id = R.drawable.ic_x),
                clickable = true,
                onTap = {
                    bottomSheetViewModel.hide()
                },
                iconWidth = 12.dp,
                iconHeight = 12.dp,
                backgroundSize = 40.dp
            )
        }
        Text(
            text = "Type of issue",
            textStyle = TextStyle.HEADER_H4,
            color = colorResource(id = R.color.black),
            modifier = Modifier.padding(top = 15.dp)
        )
        Button(
            shape = RoundedCornerShape(corner = CornerSize(8.dp)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 5.dp),
            onClick = {
                focusManager.clearFocus()
                issueViewModel.show {
                    IssueSheet(
                        issueViewModel,
                        listOf(
                            IssueItem(Issue.ITEM) { selectedIssue = Issue.ITEM },
                            IssueItem(Issue.PRICE) { selectedIssue = Issue.PRICE },
                            IssueItem(Issue.HOURS) { selectedIssue = Issue.HOURS },
                            IssueItem(Issue.WAIT_TIMES) { selectedIssue = Issue.WAIT_TIMES },
                            IssueItem(Issue.DESCRIPTION) { selectedIssue = Issue.DESCRIPTION },
                            IssueItem(Issue.OTHER) { selectedIssue = Issue.OTHER },
                        )
                    )
                }
            },
            colors = ButtonDefaults.buttonColors(
                backgroundColor = colorResource(id = R.color.gray01),
                contentColor = colorResource(id = R.color.black)
            )
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = issueMap[selectedIssue]!!,
                    textStyle = TextStyle.HEADER_H5,
                    color = if (selectedIssue == Issue.NONE) colorResource(id = R.color.gray05) else colorResource(
                        id = R.color.black
                    )
                )
                Icon(
                    painter = painterResource(id = R.drawable.ic_chevron_down_small),
                    contentDescription = ""
                )
            }
        }

        Text(
            text = "Description",
            textStyle = TextStyle.HEADER_H4,
            color = colorResource(id = R.color.black),
            modifier = Modifier.padding(top = 15.dp, bottom = 5.dp)
        )
        TextField(
            value = textEntry,
            placeholder = "Tell us what's wrong...",
            onValueChange = { textEntry = it },
            backgroundColor = colorResource(R.color.gray01),
            singleLine = false,
            isSentence = true
        )

        Button(
            shape = RoundedCornerShape(corner = CornerSize(24.dp)),
            modifier = Modifier
                .height(64.dp)
                .fillMaxWidth()
                .padding(top = 16.dp),
            onClick = {},
            enabled = textEntry.isNotEmpty() && selectedIssue != Issue.NONE,
            colors = ButtonDefaults.buttonColors(
                backgroundColor = colorResource(id = R.color.eateryBlue),
                contentColor = colorResource(id = R.color.white),
                disabledBackgroundColor = colorResource(id = R.color.gray01),
                disabledContentColor = colorResource(id = R.color.white)
            )
        ) {
            Row(verticalAlignment = Alignment.CenterVertically)
            {
                Text(
                    text = "Submit",
                    textStyle = TextStyle.HEADER_H4,
                    color = colorResource(id = R.color.white)
                )
            }
        }

        Spacer(modifier = Modifier.height(65.dp))
    }
}

@Composable
fun IssueSheet(issueViewModel: BottomSheetViewModel, items: List<IssueItem>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 8.dp)
    ) {
        for (i in items.indices) {
            val it = items[i]
            SettingsOption(
                title = issueMap[it.issue]!!,
                onClick = {
                    it.onClick()
                    issueViewModel.hide()
                },
                pointerIcon = null
            )
            if (i < items.size - 1)
                SettingsLineSeparator()
        }

        Spacer(modifier = Modifier.height(65.dp))
    }
}