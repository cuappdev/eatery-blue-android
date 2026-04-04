package com.cornellappdev.android.eatery.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.ButtonDefaults.buttonColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cornellappdev.android.eatery.R
import com.cornellappdev.android.eatery.ui.components.settings.SettingsLineSeparator
import com.cornellappdev.android.eatery.ui.theme.EateryBlueTypography
import com.cornellappdev.android.eatery.ui.theme.currentColors
import com.cornellappdev.android.eatery.ui.viewmodels.ThemeViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ThemeSheet(viewModel : ThemeViewModel = hiltViewModel(),
                onDismiss : () -> Unit)
{
    val coroutineScope = rememberCoroutineScope()
    var darkMode by remember { mutableStateOf(false) }
    var lightMode by remember { mutableStateOf(false) }
    var systemMode by remember { mutableStateOf(false) }
    val isSystemDark = isSystemInDarkTheme()

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = currentColors.backgroundDefault)
                .padding(
                    horizontal = 16.dp,
                    vertical = 24.dp
                )
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.SpaceAround
        )
        {
            Text(
                "Display",
                style = EateryBlueTypography.h4,
                color = currentColors.textPrimary,
                modifier = Modifier.padding(vertical = 16.dp)
            )

            ThemeRow(
                text = "Light",
                isSelected = lightMode == true,
                iconId = R.drawable.light_mode,
                onClick = {
                    darkMode = false
                    lightMode = true
                    systemMode=false
                }
            )
            SettingsLineSeparator()
            ThemeRow(
                text = "Dark",
                isSelected = darkMode == true,
                iconId = R.drawable.dark_mode,
                onClick = {
                    darkMode = true
                    lightMode = false
                    systemMode=false
                }
            )
            SettingsLineSeparator()
            ThemeRow(
                text = "Device Theme",
                isSelected = systemMode == true,
                iconId = R.drawable.system_setting,
                onClick = {
                    darkMode = false
                    lightMode = false
                    systemMode = true
                }
            )

            Button(
                onClick = {
                    if (darkMode) {
                        viewModel.toggleDarkMode()
                    } else if (lightMode) {
                        viewModel.toggleLightMode()
                    } else if (systemMode) {
                        viewModel.toggleSystemMode()
                    }
                    onDismiss()
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(32.dp),
                colors = buttonColors(
                    containerColor = currentColors.backgroundSecondary,
                    contentColor = currentColors.textPrimary
                )
            )
            {
                Text(
                    "Done", fontWeight = FontWeight.Bold, textAlign = TextAlign.Center,
                    color = currentColors.oppTextPrimary
                )
            }


        }
    }






@Composable
fun ThemeRow(text: String, iconId : Int, isSelected : Boolean, onClick : () -> Unit)
{
    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp)
        .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically)

    {
        Icon(
            painter = painterResource(id = iconId),
            tint = currentColors.textPrimary,
            contentDescription = null,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(text = text,
            style = EateryBlueTypography.body1,
            color = currentColors.textPrimary,
            modifier = Modifier.weight(1f))
        RadioButton(selected = isSelected,
            onClick = {
                onClick()
            })
    }

}