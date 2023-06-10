package com.rokoblak.gittrendingcompose.ui.screens.repodetails.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rokoblak.gittrendingcompose.ui.common.AppThemePreviews
import com.rokoblak.gittrendingcompose.ui.common.PreviewDataUtils
import com.rokoblak.gittrendingcompose.ui.common.composables.ErrorCell
import com.rokoblak.gittrendingcompose.ui.screens.repodetails.RepoDetailsAction
import com.rokoblak.gittrendingcompose.ui.screens.reposlisting.composables.LoadingCell
import com.rokoblak.gittrendingcompose.ui.theme.GitTrendingComposeTheme

sealed interface RepoContentUIState {
    object Loading : RepoContentUIState
    data class Error(val isNoConnection: Boolean) : RepoContentUIState
    data class Loaded(
        val header: RepoHeaderCellData,
    ) : RepoContentUIState
}

@Composable
fun RepoDetailsContent(state: RepoContentUIState, onAction: (RepoDetailsAction) -> Unit) {
    when (state) {
        is RepoContentUIState.Error -> {
            ErrorCell(isNoConnection = state.isNoConnection) {
                onAction(RepoDetailsAction.RetryClicked)
            }
        }

        RepoContentUIState.Loading -> {
            LoadingCell()
        }

        is RepoContentUIState.Loaded -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                RepoHeaderCell(data = state.header)
            }
        }
    }
}

@Preview
@AppThemePreviews
@Composable
private fun RepoDetailsContentPreview() {
    GitTrendingComposeTheme {
        val state = RepoContentUIState.Loaded(
            header = PreviewDataUtils.repoHeaderData,
        )
        RepoDetailsContent(state = state, onAction = {})
    }
}