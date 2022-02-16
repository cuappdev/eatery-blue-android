package com.appdev.eateryblueandroid.ui.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class HomeTabViewModel : ViewModel() {
    sealed class State {
        object EateryListVisible: State()
        object EateryDetailVisible: State()
        object ExpandedSectionVisible: State()
        object SearchScreenVisible: State()
    }

    private var _state = MutableStateFlow<State>(State.EateryListVisible)
    val state = _state.asStateFlow()

    fun transitionEateryDetail() {
        _state.value = State.EateryDetailVisible
    }

    fun transitionEateryList() {
        _state.value = State.EateryListVisible
    }

    fun transitionExpandedSection() {
        _state.value = State.ExpandedSectionVisible
    }
    fun transitionSearchScreen() {
        _state.value = State.SearchScreenVisible
    }
}