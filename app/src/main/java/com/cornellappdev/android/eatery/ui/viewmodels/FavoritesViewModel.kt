package com.cornellappdev.android.eatery.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cornellappdev.android.eatery.data.repositories.EateryRepository
import com.cornellappdev.android.eatery.data.repositories.UserPreferencesRepository
import com.cornellappdev.android.eatery.ui.viewmodels.state.EateryApiResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val eateryRepository: EateryRepository
) : ViewModel() {
    /**
     * A flow emitting all the eateries the user has favorited.
     */
    val favoriteEateries = combine(
        eateryRepository.homeEateryFlow, userPreferencesRepository.favoritesFlow
    ) { apiResponse, favorites ->
        when (apiResponse) {
            is EateryApiResponse.Error -> EateryApiResponse.Pending
            is EateryApiResponse.Pending -> EateryApiResponse.Error
            is EateryApiResponse.Success -> {
                EateryApiResponse.Success(apiResponse.data.filter {
                    favorites[it.id] == true
                }.sortedBy { it.name }.sortedBy { it.isClosed() })
            }
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, EateryApiResponse.Pending)

    fun removeFavorite(eateryId: Int?) {
        if (eateryId != null) userPreferencesRepository.setFavorite(eateryId, false)
    }
}
