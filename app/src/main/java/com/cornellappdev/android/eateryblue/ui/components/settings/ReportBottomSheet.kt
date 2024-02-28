package com.cornellappdev.android.eateryblue.ui.components.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cornellappdev.android.eateryblue.ui.theme.EateryBlue
import com.cornellappdev.android.eateryblue.ui.theme.EateryBlueTypography
import com.cornellappdev.android.eateryblue.ui.theme.GrayFive
import com.cornellappdev.android.eateryblue.ui.theme.GrayOne
import com.cornellappdev.android.eateryblue.ui.theme.GrayZero
import kotlinx.coroutines.launch

enum class Issue(val option: String) {
    ITEM("Inaccurate or missing item"),
    PRICE("Different price than listed"),
    HOURS("Incorrect hours"),
    WAIT_TIMES("Inaccurate wait times"),
    DESCRIPTION("Inaccurate description"),
    OTHER("Other")
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ReportBottomSheet(
    issue: Issue?,
    eateryid: Int?,
    sendReport: (issue: String, report: String, eateryid: Int?) -> Unit,
    hide: () -> Unit
) {
    val focusManager = LocalFocusManager.current
    val (textEntry, setTextEntry) = remember { mutableStateOf("") }
    val (selectedIssue, setSelectedIssue) = remember { mutableStateOf(issue) }
    var isSending by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val modalBottomSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true,
    )
    ModalBottomSheetLayout(
        sheetState = modalBottomSheetState,
        sheetShape = RoundedCornerShape(
            bottomStart = 0.dp,
            bottomEnd = 0.dp,
            topStart = 12.dp,
            topEnd = 12.dp
        ),
        sheetElevation = 8.dp,
        sheetContent = {
            Column {
                Row(modifier = Modifier.padding(top = 12.dp)) {
                    Spacer(Modifier.weight(1f, true))
                    Divider(Modifier.weight(0.75f, true), color = GrayOne, thickness = 3.dp)
                    Spacer(Modifier.weight(1f, true))
                }

                IssueBottomSheet(Issue.values(), setSelectedIssue) {
                    coroutineScope.launch {
                        modalBottomSheetState.hide()
                    }
                }
            }
        },
        content = {
            Column {
                Row(modifier = Modifier.statusBarsPadding()) {
                    Spacer(Modifier.weight(1f, true))
                    Divider(Modifier.weight(0.75f, true), color = GrayOne, thickness = 3.dp)
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
                            onClick = {
                                hide()
                            },
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
                            coroutineScope.launch {
                                modalBottomSheetState.show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = GrayZero,
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

                    val interactionSource = remember { MutableInteractionSource() }
                    BasicTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(0.7f),
                        value = textEntry,
                        onValueChange = setTextEntry,
                        keyboardActions = KeyboardActions(onDone = { onSubmit() }),
                        interactionSource = interactionSource,
                        textStyle = TextStyle(
                            color = Color.Black,
                            fontWeight = FontWeight.Medium,
                            fontSize = 16.sp
                        ),
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Sentences,
                            autoCorrect = false,
                        ),
                    ) { innerTextField ->
                        Surface(shape = RoundedCornerShape(8.dp), color = GrayZero) {
                            TextFieldDefaults.TextFieldDecorationBox(
                                value = textEntry,
                                innerTextField = innerTextField,
                                interactionSource = interactionSource,
                                enabled = true,
                                singleLine = false,
                                placeholder = {
                                    Text(
                                        text = "Tell us what's wrong...",
                                        style = EateryBlueTypography.h6,
                                        color = GrayFive
                                    )
                                },
                                colors = TextFieldDefaults.textFieldColors(
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent
                                ),
                                visualTransformation = VisualTransformation.None,
                            )
                        }
                    }

                    Button(
                        shape = RoundedCornerShape(corner = CornerSize(24.dp)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp)
                            .height(48.dp),
                        onClick = {
                            if (!isSending) {
                                isSending = true
                                sendReport(selectedIssue!!.option, textEntry, eateryid)
                                isSending = false
                            }
                            hide()
                            setTextEntry("")
                            setSelectedIssue(issue)
                        },
                        enabled = textEntry.isNotEmpty() && selectedIssue != null && !isSending,
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = EateryBlue,
                            contentColor = Color.White,
                            disabledBackgroundColor = GrayOne,
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
    )
}

@Composable
fun IssueBottomSheet(items: Array<Issue>, setIssue: (Issue) -> Unit, hide: () -> Unit) {
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
