package com.rokoblak.gittrendingcompose.ui.reposlisting.composables

import kotlinx.collections.immutable.ImmutableList

sealed interface GitReposListingData {
    object Initial: GitReposListingData
    data class Error(val isNoConnection: Boolean): GitReposListingData
    data class Loaded(val items: ImmutableList<RepoDisplayData>, val showLoadingAtEnd: Boolean): GitReposListingData
}

// TODO: Composable