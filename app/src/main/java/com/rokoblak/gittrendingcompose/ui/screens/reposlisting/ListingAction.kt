package com.rokoblak.gittrendingcompose.ui.screens.reposlisting

import com.rokoblak.gittrendingcompose.ui.screens.repodetails.RepoDetailsRoute


sealed interface ListingAction {
    object RefreshTriggered : ListingAction
    object NextPageTriggerReached : ListingAction
    data class OpenRepo(val input: RepoDetailsRoute.Input): ListingAction
    data class SetDarkMode(val enabled: Boolean) : ListingAction
}