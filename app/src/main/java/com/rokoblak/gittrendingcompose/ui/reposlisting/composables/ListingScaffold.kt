package com.rokoblak.gittrendingcompose.ui.reposlisting.composables

data class ListingScaffoldUIState(
    val drawer: ListingDrawerUIState,
    val innerContent: GitReposListingData,
)

// TODO: Composable