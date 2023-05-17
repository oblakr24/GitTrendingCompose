package com.rokoblak.gittrendingcompose.ui.reposlisting.composables

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rokoblak.gittrendingcompose.ui.common.verticalScrollbar
import com.rokoblak.gittrendingcompose.ui.reposlisting.ListingAction
import kotlinx.collections.immutable.ImmutableList

sealed interface GitReposListingData {
    object Initial: GitReposListingData
    data class Error(val isNoConnection: Boolean): GitReposListingData
    data class Loaded(val items: ImmutableList<RepoDisplayData>, val showLoadingAtEnd: Boolean): GitReposListingData
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun GitReposListing(data: GitReposListingData, onAction: (ListingAction) -> Unit) {
    when (data) {
        is GitReposListingData.Error -> {
            // TODO: Show error cell
        }
        GitReposListingData.Initial -> {
            // TODO: Show initial cell
        }
        is GitReposListingData.Loaded -> {
            val lazyListState = rememberLazyListState()
            LazyColumn(state = lazyListState, modifier = Modifier.verticalScrollbar(lazyListState)) {
                items(
                    count = data.items.size,
                    key = { data.items[it].id },
                    itemContent = { idx ->
                        val item = data.items[idx]
                        RepoDisplay(
                            modifier = Modifier.animateItemPlacement(),
                            data = item,
                        )
                        if (idx < data.items.lastIndex) {
                            Divider(startIndent = 12.dp, color = MaterialTheme.colors.onSurface.copy(alpha = 0.1f))
                        }
                    }
                )
                if (data.showLoadingAtEnd) {
                    // TODO: Show loading cell
                }
            }
        }
    }
}