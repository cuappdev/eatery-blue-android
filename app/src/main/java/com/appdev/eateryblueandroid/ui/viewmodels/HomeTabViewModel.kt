package com.appdev.eateryblueandroid.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.appdev.eateryblueandroid.models.Eatery
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class HomeTabViewModel: ViewModel() {
    sealed class State {
        object EateryListVisible: State()
        object EateryDetailVisible: State()
    }

    private var _state = MutableStateFlow<State>(State.EateryListVisible)
    val state = _state.asStateFlow()

    fun transitionEateryDetail() {
        _state.value = State.EateryDetailVisible
    }

    fun transitionEateryList() {
        _state.value = State.EateryListVisible
    }
}