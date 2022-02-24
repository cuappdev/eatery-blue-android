package com.appdev.eateryblueandroid.ui.viewmodels

import androidx.compose.runtime.Composable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class BottomSheetViewModel {
    sealed class State {
        object Hidden: State()
        data class Hiding(val contents: @Composable() () -> Unit): State()
        data class Visible(val contents: @Composable () () -> Unit): State()
    }

    private var _state = MutableStateFlow<State>(State.Hidden)
    val state = _state.asStateFlow()

    fun show(contents: @Composable () () -> Unit) {
        _state.value = State.Visible(contents)
    }

    fun hide() {
        val visibleState = _state.value as? State.Visible ?: return
        _state.value = State.Hiding(visibleState.contents)
    }

    fun hidden() {
        _state.value = State.Hidden
    }
}