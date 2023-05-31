package com.rokoblak.gittrendingcompose.ui.screens.main

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.rokoblak.gittrendingcompose.ui.screens.reposlisting.ListingReposRoute
import com.rokoblak.gittrendingcompose.ui.theme.GitTrendingComposeTheme

data class MainScreenUIState(
    val isDarkTheme: Boolean?,
)

@Composable
fun MainScreen(viewModel: MainViewModel = hiltViewModel()) {
    val state = viewModel.uiState.collectAsState(MainScreenUIState(isDarkTheme = null)).value

    val navController = rememberNavController()

    GitTrendingComposeTheme(overrideDarkMode = state.isDarkTheme) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background,
        ) {
            MainNavHostContainer(navController)
        }
    }
}

/**
 * I included the initial setup for the navigation and routing, since even though this is a single-page app,
 * it is good to set it up early so that we have a good baseline for any further additions.
 */
@Composable
private fun MainNavHostContainer(navController: NavHostController) {
    NavHost(navController = navController, startDestination = ListingReposRoute.route) {
        ListingReposRoute.register(this, navController)
    }
}