package com.appdev.eateryblueandroid.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import com.appdev.eateryblueandroid.ui.components.core.Text
import com.appdev.eateryblueandroid.ui.viewmodels.EateryDetailViewModel
import com.appdev.eateryblueandroid.ui.viewmodels.ExpandedSectionViewModel

@Composable
fun ExpandedSectionScreen(
    expandedSectionViewModel: ExpandedSectionViewModel,
    hideSection: () -> Unit
) {

    val state = expandedSectionViewModel.state.collectAsState()
    state.value.let {
        when (it) {
            is ExpandedSectionViewModel.State.Empty ->
                androidx.compose.material.Text("Empty")
            is ExpandedSectionViewModel.State.Data ->
                androidx.compose.material.Text(it.data.name)
        }
    }
    BackHandler {
        hideSection()
    }
}