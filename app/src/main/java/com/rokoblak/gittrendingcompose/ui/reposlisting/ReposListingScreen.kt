package com.rokoblak.gittrendingcompose.ui.reposlisting

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import com.rokoblak.gittrendingcompose.ui.reposlisting.composables.GitReposListingData
import com.rokoblak.gittrendingcompose.ui.reposlisting.composables.ListingDrawerUIState
import com.rokoblak.gittrendingcompose.ui.reposlisting.composables.ListingScaffold
import com.rokoblak.gittrendingcompose.ui.reposlisting.composables.ListingScaffoldUIState

@Composable
fun ReposListingScreen(viewModel: ReposListingViewModel = hiltViewModel()) {
    val state = viewModel.uiState.collectAsState(
        ListingScaffoldUIState(
            ListingDrawerUIState(
                darkMode = null,
            ), innerContent = GitReposListingData.Initial
        )
    ).value

    ListingScaffold(state = state, onAction = {
        viewModel.onAction(it)
    })
}