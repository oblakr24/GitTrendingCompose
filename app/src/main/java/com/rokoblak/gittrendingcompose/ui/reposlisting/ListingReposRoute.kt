package com.rokoblak.gittrendingcompose.ui.reposlisting

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.rokoblak.gittrendingcompose.navigation.NavRoute

object ListingReposRoute : NavRoute<ReposListingViewModel> {

    override val route = "repos/"

    @Composable
    override fun viewModel(): ReposListingViewModel = hiltViewModel()

    @Composable
    override fun Content(viewModel: ReposListingViewModel) = ReposListingScreen(viewModel)
}