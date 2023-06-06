package com.rokoblak.gittrendingcompose.ui.screens.reposlisting.composables


import androidx.compose.foundation.layout.Box
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.rokoblak.gittrendingcompose.ui.common.AppThemePreviews
import com.rokoblak.gittrendingcompose.ui.common.PreviewDataUtils
import com.rokoblak.gittrendingcompose.ui.screens.reposlisting.ListingAction
import com.rokoblak.gittrendingcompose.ui.theme.GitTrendingComposeTheme
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class ListingScaffoldUIState(
    val drawer: ListingDrawerUIState,
    val innerContent: GitReposListingData,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListingScaffoldMaterial(
    state: ListingScaffoldUIState,
    drawerState: DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed),
    onAction: (ListingAction) -> Unit,
) {
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            val coroutineScope = rememberCoroutineScope()
            ModalDrawerSheet {
                ListingDrawer(state.drawer) {
                    coroutineScope.launch {
                        drawerState.close()
                        onAction(it)
                    }
                }
            }

        }
    ) {
        ListingScaffoldContent(state = state.innerContent, drawerState = drawerState, onAction = onAction)
    }
}

@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun ListingScaffoldContent(
    state: GitReposListingData,
    drawerState: DrawerState,
    onAction: (ListingAction) -> Unit,
) {
    Scaffold(
        topBar = {
            val coroutineScope = rememberCoroutineScope()
            ListingTopAppbar {
                coroutineScope.launch {
                    drawerState.open()
                }
            }
        }
    ) { paddingValues ->
        paddingValues.calculateBottomPadding()

        val refreshScope = rememberCoroutineScope()

        var refreshing by remember { mutableStateOf(false) }

        val pullRefreshState = rememberPullRefreshState(refreshing, {
            refreshScope.launch {
                refreshing = true
                onAction(ListingAction.RefreshTriggered)
                delay(500)
                refreshing = false
            }
        })
        Box(Modifier.pullRefresh(pullRefreshState)) {
            GitReposListing(state) { action ->
                onAction(action)
            }
            PullRefreshIndicator(
                refreshing,
                pullRefreshState,
                Modifier.align(Alignment.TopCenter)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@AppThemePreviews
@Composable
private fun ListingScaffoldMaterialPreview() {
    val drawerState = ListingDrawerUIState(
        darkMode = true,
    )
    val state = ListingScaffoldUIState(
        drawerState,
        innerContent = GitReposListingData.Loaded(
            items = PreviewDataUtils.randomRepos.toImmutableList(),
            showLoadingAtEnd = true
        )
    )
    GitTrendingComposeTheme {
        ListingScaffoldMaterial(state = state, drawerState = DrawerState(DrawerValue.Closed), onAction = {})
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@AppThemePreviews
@Composable
private fun ListingScaffoldMaterialWithDrawerPreview() {
    val drawerState = ListingDrawerUIState(
        darkMode = true,
    )
    val state = ListingScaffoldUIState(
        drawerState,
        innerContent = GitReposListingData.Initial
    )
    GitTrendingComposeTheme {
        ListingScaffoldMaterial(state = state, drawerState = DrawerState(DrawerValue.Open), onAction = {})
    }
}
