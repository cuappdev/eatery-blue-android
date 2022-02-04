package com.appdev.eateryblueandroid.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.appdev.eateryblueandroid.models.Eatery
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class EateryDetailViewModel: ViewModel() {
    sealed class State {
        object Empty: State()
        data class Data(val data: Eatery) : State()
    }

    private var _state = MutableStateFlow<State>(State.Empty)
    val state = _state.asStateFlow()

    fun selectEatery(eatery: Eatery) {
        _state.value = State.Data(eatery)
    }
}