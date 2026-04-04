package com.cornellappdev.android.eatery.ui.viewmodels

import android.annotation.SuppressLint
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cornellappdev.android.eatery.data.repositories.UserPreferencesRepository
import com.cornellappdev.android.eatery.data.repositories.UserRepository
import com.cornellappdev.android.eatery.ui.theme.ColorMode
import com.cornellappdev.android.eatery.ui.theme.ColorTheme
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ThemeViewModel @Inject constructor(
    private val repository: UserPreferencesRepository): ViewModel()
{
    val isDarkMode : StateFlow<Boolean?> = repository.isDarkModeFlow.stateIn(
        viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = null)

    fun toggleDarkMode()
    {
        viewModelScope.launch { repository.setDarkMode(true)}
    }
    fun toggleLightMode()
    {
        viewModelScope.launch { repository.setDarkMode(false)}
    }

    fun toggleSystemMode()
    {
        viewModelScope.launch {repository.setSystemMode()}
    }

}


