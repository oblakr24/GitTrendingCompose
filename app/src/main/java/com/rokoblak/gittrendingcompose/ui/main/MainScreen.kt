package com.rokoblak.gittrendingcompose.ui.main

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.rokoblak.gittrendingcompose.ui.reposlisting.ListingReposRoute
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

@Composable
private fun MainNavHostContainer(navController: NavHostController) {
    NavHost(navController = navController, startDestination = ListingReposRoute.route) {
        ListingReposRoute.register(this, navController)
    }
}