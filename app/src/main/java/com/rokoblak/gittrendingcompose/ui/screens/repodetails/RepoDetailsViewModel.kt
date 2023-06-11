package com.rokoblak.gittrendingcompose.ui.screens.repodetails

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.AndroidUiDispatcher
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.cash.molecule.RecompositionClock
import app.cash.molecule.launchMolecule
import com.rokoblak.gittrendingcompose.data.domain.ExpandedGitRepositoryDetails
import com.rokoblak.gittrendingcompose.data.domain.GitRepositoryDetails
import com.rokoblak.gittrendingcompose.data.repo.GitRepoDetailsRepo
import com.rokoblak.gittrendingcompose.data.repo.GitRepoDetailsRepo.*
import com.rokoblak.gittrendingcompose.data.repo.model.LoadErrorType
import com.rokoblak.gittrendingcompose.data.repo.model.LoadableResult
import com.rokoblak.gittrendingcompose.ui.navigation.RouteNavigator
import com.rokoblak.gittrendingcompose.ui.screens.repodetails.composables.RepoContentUIState
import com.rokoblak.gittrendingcompose.ui.screens.repodetails.composables.RepoHeaderCellData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RepoDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val routeNavigator: RouteNavigator,
    private val repo: GitRepoDetailsRepo,
) : ViewModel(), RouteNavigator by routeNavigator {

    private val scope = CoroutineScope(viewModelScope.coroutineContext + AndroidUiDispatcher.Main)

    private val routeInput = RepoDetailsRoute.getIdFrom(savedStateHandle)

    val uiState: StateFlow<RepoDetailsUIState> by lazy {
        scope.launchMolecule(clock = RecompositionClock.ContextClock) {
            RepoDetailsPresenter(repo.loadResults(Input(owner = routeInput.owner, repo = routeInput.repo)))
        }
    }

    fun handleAction(act: RepoDetailsAction) {
        when (act) {
            RepoDetailsAction.RetryClicked -> {
                viewModelScope.launch {
                    repo.reload()
                }
            }
        }
    }

    @SuppressLint("ComposableNaming")
    @Composable
    private fun RepoDetailsPresenter(
        repoState: Flow<LoadableResult<ExpandedGitRepositoryDetails>>
    ): RepoDetailsUIState {
        val state = repoState.collectAsState(initial = LoadableResult.Loading)
            .value

        val innerState = when (state) {
            is LoadableResult.Error -> RepoContentUIState.Error(isNoConnection = state.type == LoadErrorType.NoNetwork)
            LoadableResult.Loading -> RepoContentUIState.Loading
            is LoadableResult.Success -> state.value.createUIState()
        }

        return RepoDetailsUIState(title = routeInput.repo, innerState)
    }

    private fun ExpandedGitRepositoryDetails.createUIState(): RepoContentUIState.Loaded {
        val details = details
        return RepoContentUIState.Loaded(
            header = details.createHeaderData(),
            rawReadme = rawReadme,
        )
    }

    private fun GitRepositoryDetails.createHeaderData(): RepoHeaderCellData {
        return RepoHeaderCellData(
            authorImgUrl = authorImgUrl,
            authorName = authorName,
            title = this.name,
            subtitle = this.desc.orEmpty(),
            stars = "$stars stars",
            forks = "$forks forks",
            issues = "$issues issues",
            mainBranch = "Default branch: $defaultBranch",
        )
    }
}
