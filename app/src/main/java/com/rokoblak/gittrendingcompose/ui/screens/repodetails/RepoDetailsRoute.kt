package com.rokoblak.gittrendingcompose.ui.screens.repodetails

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.rokoblak.gittrendingcompose.ui.navigation.NavRoute
import com.rokoblak.gittrendingcompose.ui.navigation.getOrThrow


private const val KEY_OWNER = "key-owner"
private const val KEY_REPO = "key-repo"

object RepoDetailsRoute : NavRoute<RepoDetailsViewModel> {

    override val route =
        "repo/{$KEY_REPO}?$KEY_OWNER={$KEY_OWNER}"

    fun get(input: Input): String = route
        .replace("{$KEY_REPO}", input.repo)
        .replace("{$KEY_OWNER}", input.owner)

    fun getIdFrom(savedStateHandle: SavedStateHandle): Input {
        val repo = savedStateHandle.getOrThrow<String>(KEY_REPO)
        val owner = savedStateHandle.getOrThrow<String>(KEY_OWNER)
        return Input(owner = owner, repo = repo)
    }

    override fun getArguments(): List<NamedNavArgument> = listOf(
        navArgument(KEY_REPO) { type = NavType.StringType },
        navArgument(KEY_OWNER) { type = NavType.StringType },
    )

    @Composable
    override fun viewModel(): RepoDetailsViewModel = hiltViewModel()

    @Composable
    override fun Content(viewModel: RepoDetailsViewModel) = RepoDetailsScreen(viewModel)

    data class Input(
        val owner: String,
        val repo: String,
    )
}
