package com.cornellappdev.android.eateryblue.ui.viewmodels

import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cornellappdev.android.eateryblue.data.models.Eatery
import com.cornellappdev.android.eateryblue.data.models.Event
import com.cornellappdev.android.eateryblue.data.repositories.EateryRepository
import com.cornellappdev.android.eateryblue.data.repositories.UserPreferencesRepository
import com.cornellappdev.android.eateryblue.data.repositories.UserRepository
import com.cornellappdev.android.eateryblue.ui.viewmodels.state.EateryApiResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class EateryDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val eateryRepository: EateryRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    private val eateryId: Int = checkNotNull(savedStateHandle["eateryId"])

    var eatery: State<EateryApiResponse<Eatery>> = mutableStateOf(EateryApiResponse.Pending)

    var isFavorite by mutableStateOf(false)
        private set

    init {
        openEatery(eateryId)
    }

    private fun openEatery(eateryId: Int) {
        // Technically, this isn't using the Flow architecture correctly, but it's safe to assume
        //  this will work since this screen is only accessible after eatery info is loaded.
        eatery = eateryRepository.getEateryState(eateryId)
        isFavorite = userPreferencesRepository.favoritesFlow.value[eateryId] == true
    }

    fun toggleFavorite() {
        userPreferencesRepository.setFavorite(eateryId, !isFavorite)
        isFavorite = !isFavorite
    }

    fun sendReport(issue: String, report: String, eateryid: Int?) = viewModelScope.launch {
        userRepository.sendReport(issue, report, eateryid)
    }

    private val eateryInstance = (eatery.value as? EateryApiResponse.Success)?.data

    private val _curMeal = MutableStateFlow(getCurMeal())
    var curMeal = _curMeal.asStateFlow()

    private fun getCurMeal() : Event?{
        return eateryInstance?.getCurrentDisplayedEvent()
    }
}
