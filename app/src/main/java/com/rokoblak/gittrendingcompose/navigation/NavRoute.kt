package com.rokoblak.gittrendingcompose.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable

/**
 * A route the app can navigate to
 */
interface NavRoute<T : RouteNavigator> {

    val route: String

    @Composable
    fun Content(viewModel: T)

    @Composable
    fun viewModel(): T

    fun getArguments(): List<NamedNavArgument> = listOf()

    fun register(builder: NavGraphBuilder, navHostController: NavHostController) {
        builder.composable(route, getArguments()) {
            val viewModel = viewModel()
            val viewStateAsState by viewModel.navigationState.collectAsState()

            LaunchedEffect(viewStateAsState) {
                updateNavigationState(navHostController, viewStateAsState, viewModel::onNavigated)
            }
            Content(viewModel)
        }
    }

    private fun updateNavigationState(
        navHostController: NavHostController,
        navigationState: NavigationState,
        onNavigated: (navState: NavigationState) -> Unit,
    ) {
        when (navigationState) {
            is NavigationState.NavigateToRoute -> {
                navHostController.navigate(navigationState.route)
                onNavigated(navigationState)
            }
            is NavigationState.PopToRoute -> {
                navHostController.popBackStack(navigationState.staticRoute, false)
                onNavigated(navigationState)
            }
            is NavigationState.NavigateUp -> {
                navHostController.navigateUp()
            }
            is NavigationState.Idle -> Unit
        }
    }
}