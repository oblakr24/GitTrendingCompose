package com.rokoblak.gittrendingcompose.ui.screens.reposlisting

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import com.rokoblak.gittrendingcompose.ui.screens.reposlisting.composables.GitReposListingData
import com.rokoblak.gittrendingcompose.ui.screens.reposlisting.composables.ListingDrawerUIState
import com.rokoblak.gittrendingcompose.ui.screens.reposlisting.composables.ListingScaffoldMaterial
import com.rokoblak.gittrendingcompose.ui.screens.reposlisting.composables.ListingScaffoldUIState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReposListingScreen(viewModel: ReposListingViewModel = hiltViewModel()) {
    val state = viewModel.uiState.collectAsState(
        ListingScaffoldUIState(
            ListingDrawerUIState(
                darkMode = null,
            ), innerContent = GitReposListingData.Initial
        )
    ).value
    ListingScaffoldMaterial(state = state, onAction = {
        viewModel.onAction(it)
    })
}
