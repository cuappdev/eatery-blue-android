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
import com.cornellappdev.android.eateryblue.data.repositories.UserRepository
import com.cornellappdev.android.eateryblue.ui.viewmodels.state.EateryApiResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EateryDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val eateryRepository: EateryRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    private val eateryId: Int = checkNotNull(savedStateHandle["eateryId"])

    var eatery: StateFlow<EateryApiResponse<Eatery>> = eateryRepository.eateryFlow.map { eateries ->
        when (eateries) {
            is EateryApiResponse.Pending -> EateryApiResponse.Pending

            is EateryApiResponse.Error -> EateryApiResponse.Error

            is EateryApiResponse.Success -> {
                EateryApiResponse.Success(eateries.data.first {
                    it.id == eateryId
                })
            }
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        EateryApiResponse.Pending
    )
        private set

    var isFavorite by mutableStateOf(false)
        private set

    fun toggleFavorite() = viewModelScope.launch {
        userPreferencesRepository.setFavorite(eateryId, !isFavorite)
        isFavorite = !isFavorite
    }

    fun sendReport(issue: String, report: String, eateryid: Int?) = viewModelScope.launch {
        userRepository.sendReport(issue, report, eateryid)
    }
}
