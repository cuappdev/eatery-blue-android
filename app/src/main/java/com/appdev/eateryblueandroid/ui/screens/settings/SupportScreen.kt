package com.appdev.eateryblueandroid.ui.screens.settings

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import com.appdev.eateryblueandroid.R
import com.appdev.eateryblueandroid.ui.components.core.Text
import com.appdev.eateryblueandroid.ui.components.core.TextStyle
import com.appdev.eateryblueandroid.ui.components.general.BottomSheet
import com.appdev.eateryblueandroid.ui.components.settings.DropdownOption
import com.appdev.eateryblueandroid.ui.screens.SettingsLineSeparator
import com.appdev.eateryblueandroid.ui.viewmodels.BottomSheetViewModel
import com.appdev.eateryblueandroid.ui.viewmodels.ProfileViewModel

@Composable
fun SupportScreen(profileViewModel: ProfileViewModel) {
    val context = LocalContext.current
    fun onBack() {
        profileViewModel.transitionSettings()
    }

    val bottomSheetViewModel = BottomSheetViewModel()
    val issueViewModel = BottomSheetViewModel()
    val interactionSource = MutableInteractionSource()
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(start = 16.dp, end = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 41.dp, bottom = 5.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_leftarrow),
                contentDescription = null,
                tint = colorResource(id = R.color.black),
                modifier = Modifier
                    .clickable(
                        onClick = { onBack() },
                        interactionSource = interactionSource,
                        indication = null
                    )
                    .clip(CircleShape)

            )
        }

        Text(
            text = "Support",
            color = colorResource(id = R.color.eateryBlue),
            textStyle = TextStyle.HEADER_H1,
            modifier = Modifier.padding(top = 7.dp)
        )
        Text(
            text = "Report issues and contact Cornell AppDev",
            textStyle = TextStyle.APPDEV_BODY_MEDIUM,
            color = colorResource(id = R.color.gray06),
            modifier = Modifier.padding(top = 7.dp, bottom = 24.dp)
        )

        Text(
            text = "Make Eatery Better",
            color = colorResource(id = R.color.black),
            textStyle = TextStyle.HEADER_H3,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        Text(
            text = "Help us improve Eatery by letting us know what’s wrong.",
            color = colorResource(id = R.color.gray05),
            textStyle = TextStyle.BODY_MEDIUM,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        Button(
            shape = RoundedCornerShape(corner = CornerSize(24.dp)),
            modifier = Modifier
                .height(48.dp)
                .fillMaxWidth(),
            onClick = { beginReport(Issue.NONE, bottomSheetViewModel, issueViewModel) },
            colors = ButtonDefaults.buttonColors(
                backgroundColor = colorResource(id = R.color.eateryBlue),
                contentColor = colorResource(id = R.color.white)
            )
        ) {
            Row(verticalAlignment = Alignment.CenterVertically)
            {
                Icon(painter = painterResource(id = R.drawable.ic_report_bubble), "")
                Text(
                    text = "Report an Issue",
                    textStyle = TextStyle.HEADER_H4,
                    color = colorResource(id = R.color.white),
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }

        // Send email Section
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Shoot us an email",
                textStyle = TextStyle.BODY_SEMIBOLD,
                color = colorResource(id = R.color.eateryBlue),
                modifier = Modifier
                    .padding(end = 6.67.dp, top = 12.dp)
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onTap = {
                                val intent = Intent(Intent.ACTION_SEND)
                                intent.data = Uri.parse("mailto:")
                                intent.type = "text/plain"

                                intent.putExtra(Intent.EXTRA_EMAIL, "team@cornellappdev.com")
                                intent.putExtra(Intent.EXTRA_SUBJECT, "Eatery - Reporting an Issue")
                                startActivity(context, intent, null)
                            }
                        )
                    }
            )
            Icon(
                painter = painterResource(id = R.drawable.ic_upright_transfer_arrow),
                "",
                tint = colorResource(id = R.color.eateryBlue)
            )
        }

        Text(
            text = "Frequently Asked Questions",
            textStyle = TextStyle.HEADER_H3,
            color = colorResource(id = R.color.black),
            modifier = Modifier.padding(top = 43.dp)
        )
        DropdownOption("Why do I see wrong or empty menus?") {
            Text(
                text = stringResource(id = R.string.wrong_empty_menus),
                textStyle = TextStyle.BODY_MEDIUM,
                color = colorResource(id = R.color.gray05),
                modifier = Modifier.padding(bottom = 12.dp)
            )
            ReportButton(issue = Issue.ITEM, bottomSheetViewModel, issueViewModel)
        }
        SettingsLineSeparator()
        DropdownOption("Why is an eatery closed when it says it should be open?") {
            Text(
                text = stringResource(id = R.string.eatery_closed_when_open),
                textStyle = TextStyle.BODY_MEDIUM,
                color = colorResource(id = R.color.gray05),
                modifier = Modifier.padding(bottom = 12.dp)
            )
            ReportButton(issue = Issue.HOURS, bottomSheetViewModel, issueViewModel)
        }
        SettingsLineSeparator()
        DropdownOption("Why is the wait time longer?") {
            Text(
                text = stringResource(id = R.string.wait_time_longer),
                textStyle = TextStyle.BODY_MEDIUM,
                color = colorResource(id = R.color.gray05),
                modifier = Modifier.padding(bottom = 12.dp)
            )
            ReportButton(issue = Issue.WAIT_TIMES, bottomSheetViewModel, issueViewModel)
        }
        SettingsLineSeparator()
        DropdownOption("Why can’t I order food on Eatery?") {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = stringResource(id = R.string.order_on_eatery),
                    textStyle = TextStyle.BODY_MEDIUM,
                    color = colorResource(id = R.color.gray05)
                )
                Row {
                    Text(
                        text = "send them an email ",
                        textStyle = TextStyle.BODY_MEDIUM,
                        color = colorResource(id = R.color.eateryBlue),
                        modifier = Modifier.clickable(onClick = {
                            val intent = Intent(Intent.ACTION_SEND)
                            intent.data = Uri.parse("mailto:")
                            intent.type = "text/plain"

                            intent.putExtra(Intent.EXTRA_EMAIL, "dining@cornell.edu")
                            intent.putExtra(Intent.EXTRA_SUBJECT, "Ordering Food on Eatery")
                            startActivity(context, intent, null)
                        })
                    )
                    Text(
                        text = ":-)",
                        textStyle = TextStyle.BODY_MEDIUM,
                        color = colorResource(id = R.color.gray05)
                    )
                    Spacer(modifier = Modifier.height(65.dp))
                }
            }
        }
        Spacer(modifier = Modifier.height(65.dp))
    }
    BottomSheet(bottomSheetViewModel = bottomSheetViewModel)
    BottomSheet(bottomSheetViewModel = issueViewModel)

    BackHandler {
        onBack()
    }
}

@Composable
private fun ReportButton(
    issue: Issue,
    bottomSheetViewModel: BottomSheetViewModel,
    issueSheetViewModel: BottomSheetViewModel
) {
    Button(
        shape = RoundedCornerShape(corner = CornerSize(17.dp)),
        modifier = Modifier
            .height(50.dp)
            .width(150.dp)
            .padding(bottom = 16.dp),
        onClick = { beginReport(issue, bottomSheetViewModel, issueSheetViewModel) },
        colors = ButtonDefaults.buttonColors(
            backgroundColor = colorResource(id = R.color.gray00),
            contentColor = colorResource(id = R.color.black)
        )
    ) {
        Row(verticalAlignment = Alignment.CenterVertically)
        {
            Icon(painter = painterResource(id = R.drawable.ic_mini_report_bubble), "")
            Text(
                text = "Report an Issue",
                textStyle = TextStyle.BODY_SEMIBOLD,
                color = colorResource(id = R.color.black),
                modifier = Modifier.padding(start = 5.33.dp)
            )
        }
    }
}


fun beginReport(
    issue: Issue,
    bottomSheetViewModel: BottomSheetViewModel,
    issueSheetViewModel: BottomSheetViewModel
) {
    bottomSheetViewModel.show {
        ReportSheet(
            issue = issue,
            bottomSheetViewModel = bottomSheetViewModel,
            issueViewModel = issueSheetViewModel
        )
    }
}