package com.appdev.eateryblueandroid.ui.viewmodels

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appdev.eateryblueandroid.models.Eatery
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch


class SearchViewModel(
): ViewModel() {
    sealed class State{
        object Loading : State()
        object NothingTyped: State()
        object WordsTyped: State()
        data class Failure(val errorMsg: String) : State()
    }
    private var _state = MutableStateFlow<SearchViewModel.State>(SearchViewModel.State.Loading)
    val state = _state.asStateFlow()

    val typedText = mutableStateOf("")

    fun transitionSearchLoading() {
        _state.value = SearchViewModel.State.Loading
    }
    fun transitionSearchWordsTyped() {
        _state.value = SearchViewModel.State.WordsTyped
    }
    fun transitionSearchNothingTyped() {
        _state.value = SearchViewModel.State.NothingTyped
    }
    fun onTextChange(input : String){
        typedText.value = input
    }
    fun getSearchText(): MutableState<String> {
        return typedText
    }
}