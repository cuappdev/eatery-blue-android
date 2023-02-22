package com.cornellappdev.android.eateryblue.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cornellappdev.android.eateryblue.data.repositories.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SupportViewModel @Inject constructor(
    private val userRepository: UserRepository,
) : ViewModel() {
    fun sendReport(issue: String, report: String) = viewModelScope.launch {
        userRepository.sendReport(issue, report)
    }
}
