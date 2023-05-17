package com.rokoblak.gittrendingcompose.ui.reposlisting


sealed interface ListingAction {
    object RefreshTriggered : ListingAction
    object NextPageTriggerReached : ListingAction
    data class SetDarkMode(val enabled: Boolean) : ListingAction
}