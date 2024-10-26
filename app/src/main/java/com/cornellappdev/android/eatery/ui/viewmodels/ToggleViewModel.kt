package com.cornellappdev.android.eatery.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.cornellappdev.android.eatery.data.repositories.EateryRepository
import com.cornellappdev.android.eatery.data.repositories.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class ToggleViewModel @Inject constructor(
) : ViewModel() {
    //    default state is left side 'on'
//    so _isToggled = false corresponds to left side 'on'
    private val _isToggled = MutableStateFlow(false) // initial toggle state
    val isToggled: StateFlow<Boolean> = _isToggled

    fun toggle() {
        _isToggled.value = !_isToggled.value
    }
}