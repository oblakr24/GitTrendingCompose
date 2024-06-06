package com.rokoblak.gittrendingcompose.ui.screens.repodetails.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import dev.jeziellago.compose.markdowntext.MarkdownText

sealed interface RepoContentUIState {
    object Loading : RepoContentUIState
    data class Error(val isNoConnection: Boolean) : RepoContentUIState
    data class Loaded(
        val header: RepoHeaderCellData,
        val readmeFilename: String?,
        val readmeContent: String? = null,
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
                    .fillMaxWidth()
                    .padding(16.dp)
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top,
            ) {
                RepoHeaderCell(data = state.header)

                Spacer(modifier = Modifier.height(16.dp))

                if (state.readmeContent != null) {
                    Text(text = state.readmeFilename.orEmpty())
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight(), propagateMinConstraints = true
                    ) {
                        MarkdownText(
                            color = MaterialTheme.colorScheme.onBackground,
                            style = MaterialTheme.typography.labelSmall,
                            markdown = state.readmeContent
                        )
                    }
                }
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
            readmeContent = "Readme.MD content goes here",
            readmeFilename = "Readme.MD",
        )
        RepoDetailsContent(state = state, onAction = {})
    }
}