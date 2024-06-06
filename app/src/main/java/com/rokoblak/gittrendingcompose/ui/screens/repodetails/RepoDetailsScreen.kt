package com.rokoblak.gittrendingcompose.ui.screens.repodetails

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import com.rokoblak.gittrendingcompose.ui.common.composables.DetailsContent
import com.rokoblak.gittrendingcompose.ui.screens.repodetails.composables.RepoContentUIState
import com.rokoblak.gittrendingcompose.ui.screens.repodetails.composables.RepoDetailsContent

data class RepoDetailsUIState(
    val title: String,
    val inner: RepoContentUIState,
)

@Composable
fun RepoDetailsScreen(viewModel: RepoDetailsViewModel = hiltViewModel()) {
    val state = viewModel.uiState.collectAsState().value

    DetailsContent(title = state.title, onBackPressed = {
        viewModel.navigateUp()
    }) {
        RepoDetailsContent(state = state.inner, onAction = { act ->
            viewModel.handleAction(act)
        })
    }
}
