package com.rokoblak.gittrendingcompose.ui.screens.repodetails.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rokoblak.gittrendingcompose.ui.common.AppThemePreviews
import com.rokoblak.gittrendingcompose.ui.common.composables.ErrorCell
import com.rokoblak.gittrendingcompose.ui.screens.repodetails.RepoDetailsAction
import com.rokoblak.gittrendingcompose.ui.screens.reposlisting.composables.LoadingCell
import com.rokoblak.gittrendingcompose.ui.theme.GitTrendingComposeTheme

sealed interface RepoContentUIState {
    object Loading : RepoContentUIState
    data class Error(val isNoConnection: Boolean) : RepoContentUIState
    data class Loaded(
        val name: String,
        val desc: String,
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
                Text(
                    text = state.name,
                    style = MaterialTheme.typography.bodySmall,
                )

                Text(
                    text = state.desc,
                    style = MaterialTheme.typography.bodySmall,
                )
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
            name = "Repo Name",
            desc = "Repo Desc",
        )
        RepoDetailsContent(state = state, onAction = {})
    }
}