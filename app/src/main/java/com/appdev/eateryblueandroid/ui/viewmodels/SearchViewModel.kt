package com.appdev.eateryblueandroid.ui.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class SearchViewModel: ViewModel() {
    sealed class State{
        object Loading : State()
        object NothingTyped: State()
        object WordsTyped: State()
        object SearchResults: State()
    }
    private var _state = MutableStateFlow<SearchViewModel.State>(SearchViewModel.State.Loading)
    val state = _state.asStateFlow()

    fun transitionSearchLoading() {
        _state.value = SearchViewModel.State.Loading
    }

    fun transitionSearchNothinTyped() {
        _state.value = SearchViewModel.State.NothingTyped
    }

    fun transitionWordsTyped() {
        _state.value = SearchViewModel.State.WordsTyped
    }
    fun transitionSearchResults() {
        _state.value = SearchViewModel.State.SearchResults
    }
}