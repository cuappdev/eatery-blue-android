package com.appdev.eateryblueandroid.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appdev.eateryblueandroid.models.ApiResponse
import com.appdev.eateryblueandroid.models.Eatery
import com.appdev.eateryblueandroid.models.EaterySection
import com.appdev.eateryblueandroid.networking.internal.ApiService
import com.appdev.eateryblueandroid.util.initializeFavoriteMap
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.net.SocketTimeoutException

class HomeViewModel(
    fetchFromApi: Boolean
) : ViewModel() {
    sealed class State {
        object Loading : State()
        data class Failure(val errorMsg: String) : State()
        data class Data(
            val eateries: List<Eatery>,
            val sections: List<EaterySection>,
            val filters: List<String>
        ) : State()
    }

    private var _state = MutableStateFlow<State>(State.Loading)
    val state = _state.asStateFlow()

    init {
        if (fetchFromApi) {
            viewModelScope.launch {
                while (isActive) {
                    var res: ApiResponse<List<Eatery>>? = null
                    try {
                        res = ApiService.getInstance().fetchEateries()
                        if (res.success) {
                            res.data?.let { eateries ->
                                initializeFavoriteMap(eateries)
                                _state.value = State.Data(
                                    eateries = eateries,
                                    sections = eaterySections(),
                                    filters = listOf("Meal swipes", "BRBs", "Cash or credit")
                                )
                                this.cancel()
                            }
                        } else {
                            res.error?.let { _state.value = State.Failure(it) }
                        }
                    }
                    catch (h : retrofit2.HttpException) {
                        h.printStackTrace()
                        if (res != null) {
                            res.error?.let { _state.value = State.Failure(it) }
                        }
                        this.cancel()
                    }
                    catch (s : SocketTimeoutException) {
                        s.printStackTrace()
                        if (res != null) {
                            res.error?.let { _state.value = State.Failure(it) }
                        }
                        this.cancel()
                    }

                }
            }
        }
    }

    fun updateFilters(selection: List<String>){
        if (state.value is State.Data) {
            _state.value = State.Data(
                eateries = (state.value as State.Data).eateries,
                sections = (state.value as State.Data).sections,
                filters = selection
            )
        }
    }

    private fun eaterySections(): List<EaterySection> {
        return listOf(
            EaterySection("Favorite Eateries") { it.isFavorite() },
            EaterySection("Nearest to You") { it.campusArea == "West" }
        )
    }
}