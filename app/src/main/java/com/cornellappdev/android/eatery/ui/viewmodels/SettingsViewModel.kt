package com.cornellappdev.android.eatery.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cornellappdev.android.eatery.data.repositories.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userRepository: UserRepository,
) : ViewModel() {

    val isLoggedIn: StateFlow<Boolean> = userRepository.isLoggedInFlow.stateIn(
        viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = false
    )

    fun onLogout(onDone: () -> Unit = {}) {
        viewModelScope.launch {
            userRepository.logout()
            onDone()
        }
    }
}
