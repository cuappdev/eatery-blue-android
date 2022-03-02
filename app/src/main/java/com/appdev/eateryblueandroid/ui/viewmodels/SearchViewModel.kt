package com.appdev.eateryblueandroid.ui.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appdev.eateryblueandroid.models.Eatery
import com.appdev.eateryblueandroid.networking.ApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class SearchViewModel(
    fetchFromApi: Boolean
): ViewModel() {
    sealed class State{
        object Loading : State()
//        data class WordsTyped(val eateries: List<Eatery>): State()
        object NothingTyped: State()
//        object WordsTyped: State()
        data class WordsTyped(val eateries: List<Eatery>): State()
        object SearchResults: State()

        data class Failure(val errorMsg: String) : State()
    }
    private var _state = MutableStateFlow<SearchViewModel.State>(SearchViewModel.State.Loading)
    val state = _state.asStateFlow()
    val typedText = mutableStateOf("sandwich")

    init {
        if (fetchFromApi) {
            viewModelScope.launch {
                while (isActive) {
                    val res = ApiService.getInstance().fetchEateries()
                    if (res.success) {
                        res.data?.let { eateries ->
                            _state.value = State.WordsTyped(
                                eateries = eateries
                            )
                        }
                    } else {
                        res.error?.let { _state.value = State.Failure(it) }
                    }
                }
            }
        }
    }

    fun transitionSearchLoading() {
        _state.value = SearchViewModel.State.Loading
    }



    fun transitionSearchNothingTyped() {
        _state.value = SearchViewModel.State.NothingTyped
    }
    fun transitionSearchResults() {
        _state.value = SearchViewModel.State.SearchResults
    }
    fun onTextChange(input : String){
        typedText.value = input
    }
}