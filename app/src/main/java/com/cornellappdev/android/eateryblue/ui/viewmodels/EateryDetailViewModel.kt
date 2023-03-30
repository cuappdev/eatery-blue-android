package com.cornellappdev.android.eateryblue.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cornellappdev.android.eateryblue.data.models.Eatery
import com.cornellappdev.android.eateryblue.data.repositories.EateryRepository
import com.cornellappdev.android.eateryblue.data.repositories.UserPreferencesRepository
import com.cornellappdev.android.eateryblue.ui.viewmodels.state.EateryRetrievalState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EateryDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val eateryRepository: EateryRepository
) : ViewModel() {
    private val eateryId: Int = checkNotNull(savedStateHandle["eateryId"])

    var eateryRetrievalState: EateryRetrievalState by mutableStateOf(EateryRetrievalState.Pending)
        private set

    var eatery by mutableStateOf(Eatery())
        private set

    var isFavorite by mutableStateOf(false)
        private set

    init {
        queryEatery()
    }

    private fun queryEatery() = viewModelScope.launch {
        try {
            val eateryResponse = eateryRepository.getAllEateries()
            isFavorite = userPreferencesRepository.getFavorite(eateryId)
            eatery = eateryResponse.first {
                it.id == eateryId
            }
            eateryRetrievalState = EateryRetrievalState.Success
        } catch (_: Exception) {
            eateryRetrievalState = EateryRetrievalState.Error
        }
    }

    fun toggleFavorite() = viewModelScope.launch {
        userPreferencesRepository.setFavorite(eateryId, !isFavorite)
        isFavorite = !isFavorite
    }
}
