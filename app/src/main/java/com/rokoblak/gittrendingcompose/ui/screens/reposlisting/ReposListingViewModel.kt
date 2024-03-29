package com.rokoblak.gittrendingcompose.ui.screens.reposlisting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rokoblak.gittrendingcompose.data.repo.model.LoadErrorType
import com.rokoblak.gittrendingcompose.data.repo.model.LoadableResult
import com.rokoblak.gittrendingcompose.domain.usecases.DarkModeHandlingUseCase
import com.rokoblak.gittrendingcompose.domain.usecases.ReposListingUseCase
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
    private val listingUseCase: ReposListingUseCase,
    private val darkModeUseCase: DarkModeHandlingUseCase,
) : ViewModel(), RouteNavigator by routeNavigator {

    private val listingData: Flow<GitReposListingData> = listingUseCase.flow.map { loadResult ->
        when (loadResult) {
            is LoadableResult.Error -> GitReposListingData.Error(isNoConnection = loadResult.type == LoadErrorType.NoNetwork)
            LoadableResult.Loading -> GitReposListingData.Initial
            is LoadableResult.Success -> GitReposListingData.Loaded(
                loadResult.value.repos.map { it.toDisplay() }.toImmutableList(),
                showLoadingAtEnd = loadResult.value.loadingMore,
            )
        }
    }.onStart { emit(GitReposListingData.Initial) }

    val uiState = combine(darkModeUseCase.darkModeEnabled(), listingData) { darkMode, listing ->
        ListingScaffoldUIState(
            drawer = ListingDrawerUIState(
                darkMode = darkMode,
            ), innerContent = listing
        )
    }

    fun onAction(act: ListingAction) {
        viewModelScope.launch {
            when (act) {
                ListingAction.NextPageTriggerReached -> listingUseCase.loadNext()
                ListingAction.RefreshTriggered -> listingUseCase.reload()
                is ListingAction.SetDarkMode -> setDarkMode(act.enabled)
                is ListingAction.OpenRepo -> navigateToRoute(RepoDetailsRoute.get(act.input))
            }
        }
    }

    private fun setDarkMode(enabled: Boolean) = viewModelScope.launch {
        darkModeUseCase.updateDarkMode(enabled)
    }
}
