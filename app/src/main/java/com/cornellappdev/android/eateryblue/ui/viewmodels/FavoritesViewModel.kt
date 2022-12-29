package com.cornellappdev.android.eateryblue.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
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
class FavoritesViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val eateryRepository: EateryRepository
) : ViewModel() {
    var eateryRetrievalState: EateryRetrievalState by mutableStateOf(EateryRetrievalState.Pending)
        private set

    private val allEateries = mutableSetOf<Eatery>()

    var favoriteEateries = mutableStateListOf<Eatery>()
        private set

    init {
        queryFavoriteEateries()
    }

    fun queryFavoriteEateries() = viewModelScope.launch {
        try {
            val eateryResponse = eateryRepository.getAllEateries()
            if (eateryResponse.success) {
                eateryResponse.data?.let { allEateries.addAll(it) }

                val favoriteEateriesIds =
                    userPreferencesRepository.getFavoritesMap().keys
                favoriteEateries.addAll(allEateries.filter {
                    favoriteEateriesIds.contains(it.id)
                })

                eateryRetrievalState = EateryRetrievalState.Success
            }
        } catch (_: Exception) {
            eateryRetrievalState = EateryRetrievalState.Error
        }
    }

    fun removeFavorite(eateryId: Int?) = viewModelScope.launch {
        if (eateryId == null) return@launch

        userPreferencesRepository.setFavorite(eateryId, false)
        favoriteEateries.removeIf { eatery ->
            eatery.id == eateryId
        }
    }

    fun updateFavorites() = viewModelScope.launch {
        eateryRetrievalState = EateryRetrievalState.Pending
        try {
            val favoriteEateriesKeys =
                userPreferencesRepository.getFavoritesMap().keys
            favoriteEateries = allEateries.filter {
                favoriteEateriesKeys.contains(it.id)
            }.toCollection(mutableStateListOf())

            eateryRetrievalState = EateryRetrievalState.Success
        } catch (_: Exception) {
            eateryRetrievalState = EateryRetrievalState.Error
        }
    }
}
