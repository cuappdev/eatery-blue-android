package com.appdev.eateryblueandroid.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appdev.eateryblueandroid.models.Eatery
import com.appdev.eateryblueandroid.networking.ApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class EateryViewModel: ViewModel() {
    sealed class State {
        object Loading: State()
        data class Data(val data: Eatery) : State()
    }

    private var _state = MutableStateFlow<State>(State.Loading)
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            while (isActive) {
                val res = ApiService.getInstance().fetchEateries()
                if (res.success) {
                    res.data?.let { eateries ->
                        val terrace = eateries.find { eatery -> eatery.id == 33 }
                        terrace?.let {_state.value = State.Data(it) }
                    }
                }
            }
        }
    }
}