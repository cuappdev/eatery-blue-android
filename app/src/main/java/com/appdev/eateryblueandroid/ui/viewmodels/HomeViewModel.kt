package com.appdev.eateryblueandroid.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appdev.eateryblueandroid.models.Eatery
import com.appdev.eateryblueandroid.networking.ApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class HomeViewModel(
    fetchFromApi: Boolean
): ViewModel() {
    sealed class State {
        object Loading: State()
        data class Failure(val errorMsg: String) : State()
        data class Data(val data: List<HomeViewModelItem>) : State()
    }

    private var _state = MutableStateFlow<State>(State.Loading)
    val state = _state.asStateFlow()

    init {
        if (fetchFromApi) {
            viewModelScope.launch {
                while (isActive) {
                    val res = ApiService.getInstance().fetchEateries()
                    if (res.success) {
                        res.data?.let { eateries ->
                            _state.value = State.Data(listOf(
                                listOf(HomeViewModelItem.SearchBox),
                                temporaryCategories(eateries),
                                eateries.map { HomeViewModelItem.EateryItem(it) }
                            ).flatten())
                        }
                    } else {
                        res.error?.let { _state.value = State.Failure(it) }
                    }
                }
            }
        }
    }

    // TODO: Convert to middleware
    fun temporaryCategories(eateries: List<Eatery>): List<HomeViewModelItem.EateryCategory> {
        return listOf(
            HomeViewModelItem.EateryCategory(
                name = "Favorite Eateries",
                eateries = eateries.subList(0, 3).toList()
            ),
            HomeViewModelItem.EateryCategory(
                name = "Nearest to You",
                eateries = eateries.subList(4, 7).toList()
            )
        )
    }
}

sealed class HomeViewModelItem {
    object SearchBox: HomeViewModelItem()
    data class EateryCategory(val name: String, val eateries: List<Eatery>): HomeViewModelItem()
    data class EateryItem(val eatery: Eatery): HomeViewModelItem()
}