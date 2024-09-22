package com.cornellappdev.android.eatery.ui.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cornellappdev.android.eatery.data.models.Eatery
import com.cornellappdev.android.eatery.data.models.Event
import com.cornellappdev.android.eatery.data.repositories.EateryRepository
import com.cornellappdev.android.eatery.data.repositories.UserPreferencesRepository
import com.cornellappdev.android.eatery.data.repositories.UserRepository
import com.cornellappdev.android.eatery.ui.components.general.Filter
import com.cornellappdev.android.eatery.ui.viewmodels.state.EateryApiResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

//todo write this viewmodel
@HiltViewModel
class CompareMenusViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val eateryRepository: EateryRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    lateinit var eateryFlow: StateFlow<EateryApiResponse<List<Eatery>>>

//    init {
//        val eateryIds = savedStateHandle.get<List<Int>>("eateryIds") ?: listOf()
//        openEatery(eateryIds)
//    }
    public fun openEatery(eateryIds: List<Int>) {
        eateryFlow =
            eateryRepository.eateryFlow.map { apiResponse ->
                when (apiResponse) {
                    is EateryApiResponse.Success -> {
                        EateryApiResponse.Success(apiResponse.data.filter {eateryIds.contains(it.id)})
                    }
                    else -> apiResponse
                }
            }.stateIn(viewModelScope, SharingStarted.Eagerly, EateryApiResponse.Pending)
    }

    fun sendReport(issue: String, report: String, eateryid: Int?) = viewModelScope.launch {
        userRepository.sendReport(issue, report, eateryid)
    }
}