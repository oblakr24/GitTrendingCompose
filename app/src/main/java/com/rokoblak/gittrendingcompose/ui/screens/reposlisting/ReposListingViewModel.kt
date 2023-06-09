package com.rokoblak.gittrendingcompose.ui.screens.reposlisting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rokoblak.gittrendingcompose.data.repo.GitRepositoriesLoadingRepo
import com.rokoblak.gittrendingcompose.data.repo.model.LoadErrorType
import com.rokoblak.gittrendingcompose.service.PersistedStorage
import com.rokoblak.gittrendingcompose.ui.navigation.RouteNavigator
import com.rokoblak.gittrendingcompose.ui.screens.repodetails.RepoDetailsRoute
import com.rokoblak.gittrendingcompose.ui.screens.reposlisting.ReposListingUIMapper.toDisplay
import com.rokoblak.gittrendingcompose.ui.screens.reposlisting.composables.GitReposListingData
import com.rokoblak.gittrendingcompose.ui.screens.reposlisting.composables.ListingDrawerUIState
import com.rokoblak.gittrendingcompose.ui.screens.reposlisting.composables.ListingScaffoldUIState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReposListingViewModel @Inject constructor(
    private val routeNavigator: RouteNavigator,
    private val repo: GitRepositoriesLoadingRepo,
    private val storage: PersistedStorage,
) : ViewModel(), RouteNavigator by routeNavigator {

    private val listingData: Flow<GitReposListingData> = repo.loadResults.map { loadResult ->
        when (loadResult) {
            is GitRepositoriesLoadingRepo.LoadResult.LoadError -> GitReposListingData.Error(
                isNoConnection = loadResult.type == LoadErrorType.NoNetwork
            )

            is GitRepositoriesLoadingRepo.LoadResult.Loaded -> GitReposListingData.Loaded(
                loadResult.loadedItems.map { it.toDisplay() }.toImmutableList(),
                showLoadingAtEnd = loadResult.loadingMore
            )

            GitRepositoriesLoadingRepo.LoadResult.LoadingFirstPage -> GitReposListingData.Initial
        }
    }.onStart { emit(GitReposListingData.Initial) }

    val uiState = combine(storage.prefsFlow(), listingData) { prefs, listing ->
        ListingScaffoldUIState(
            drawer = ListingDrawerUIState(
                darkMode = prefs.darkMode,
            ), innerContent = listing
        )
    }

    fun onAction(act: ListingAction) {
        viewModelScope.launch {
            when (act) {
                ListingAction.NextPageTriggerReached -> repo.loadNext()
                ListingAction.RefreshTriggered -> repo.reload()
                is ListingAction.SetDarkMode -> setDarkMode(act.enabled)
                is ListingAction.OpenRepo -> navigateToRoute(RepoDetailsRoute.get(act.input))
            }
        }
    }

    private fun setDarkMode(enabled: Boolean) = viewModelScope.launch {
        storage.updateDarkMode(enabled)
    }
}
