package com.appdev.eateryblueandroid.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.appdev.eateryblueandroid.models.EaterySection
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class ExpandedSectionViewModel: ViewModel() {
    sealed class State {
        object Empty: State()
        data class Data(val data: EaterySection) : State()
    }

    private var _state = MutableStateFlow<State>(State.Empty)
    val state = _state.asStateFlow()

    fun expandSection(section: EaterySection) {
        _state.value = State.Data(section)
    }
}