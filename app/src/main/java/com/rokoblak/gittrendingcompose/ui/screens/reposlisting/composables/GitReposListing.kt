package com.rokoblak.gittrendingcompose.ui.screens.reposlisting.composables

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.rokoblak.gittrendingcompose.ui.common.composables.ErrorCell
import com.rokoblak.gittrendingcompose.ui.common.verticalScrollbar
import com.rokoblak.gittrendingcompose.ui.screens.repodetails.RepoDetailsRoute
import com.rokoblak.gittrendingcompose.ui.screens.reposlisting.ListingAction
import kotlinx.collections.immutable.ImmutableList

sealed interface GitReposListingData {
    object Initial : GitReposListingData
    data class Error(val isNoConnection: Boolean) : GitReposListingData
    data class Loaded(val items: ImmutableList<RepoDisplayData>, val showLoadingAtEnd: Boolean) :
        GitReposListingData
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun GitReposListing(data: GitReposListingData, onAction: (ListingAction) -> Unit) {
    when (data) {
        is GitReposListingData.Error -> {
            ErrorCell(isNoConnection = data.isNoConnection) {
                onAction(ListingAction.RefreshTriggered)
            }
        }

        GitReposListingData.Initial -> {
            Column(modifier = Modifier.fillMaxSize()) {
                (0..10).forEach { _ ->
                    LoadingCell()
                    Divider(
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                    )
                }
            }
        }

        is GitReposListingData.Loaded -> {
            val lazyListState = rememberLazyListState()
            ListingScrollTracker(
                state = data,
                listState = lazyListState,
                onScrollSettledNearEnd = {
                    onAction(ListingAction.NextPageTriggerReached)
                }
            )
            LazyColumn(
                state = lazyListState,
                modifier = Modifier.verticalScrollbar(lazyListState)
            ) {
                items(
                    count = data.items.size,
                    key = { data.items[it].id },
                    itemContent = { idx ->
                        val item = data.items[idx]
                        RepoDisplay(
                            modifier = Modifier
                                .animateItemPlacement()
                                .clickable {
                                    val input = RepoDetailsRoute.Input(
                                        owner = item.authorName,
                                        repo = item.name
                                    )
                                    onAction(ListingAction.OpenRepo(input))
                                },
                            data = item,
                        )
                        if (idx < data.items.lastIndex) {
                            Divider(
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                            )
                        }
                    }
                )
                if (data.showLoadingAtEnd) {
                    item {
                        LoadingCell()
                    }
                }
            }
        }
    }
}