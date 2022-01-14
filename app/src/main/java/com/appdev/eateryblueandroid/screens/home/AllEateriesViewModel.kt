package com.appdev.eateryblueandroid.screens.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appdev.eateryblueandroid.models.Eatery
import com.appdev.eateryblueandroid.networking.ApiService
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class AllEateriesViewModel: ViewModel() {
    sealed class State {
        object Loading: State()
        data class Failure(val errorMsg: String) : State()
        data class Data(val data: List<Eatery>) : State()
    }

    private var _state = MutableStateFlow<State>(State.Loading)
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            while (isActive) {
                val data = ApiService.getInstance().fetchEateries()
                //_state.value = State.Data(data.eateries)
                Log.i("qwerty", data.toString())
                // Wait one minute and re-fetch the Eateries
                delay(60 * 1000L)
            }
        }
    }
}