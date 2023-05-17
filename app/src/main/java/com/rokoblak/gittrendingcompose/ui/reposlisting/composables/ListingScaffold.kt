package com.rokoblak.gittrendingcompose.ui.reposlisting.composables

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.DrawerState
import androidx.compose.material.DrawerValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Text
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.rokoblak.gittrendingcompose.ui.common.AppThemePreviews
import com.rokoblak.gittrendingcompose.ui.common.PreviewDataUtils
import com.rokoblak.gittrendingcompose.ui.reposlisting.ListingAction
import com.rokoblak.gittrendingcompose.ui.theme.GitTrendingComposeTheme
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class ListingScaffoldUIState(
    val drawer: ListingDrawerUIState,
    val innerContent: GitReposListingData,
)

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ListingScaffold(
    state: ListingScaffoldUIState,
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    onAction: (ListingAction) -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        scaffoldState = scaffoldState,
        topBar = {
            ListingTopAppbar {
                coroutineScope.launch {
                    scaffoldState.drawerState.open()
                }
            }
        },
        drawerContent = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                ListingDrawer(state.drawer) {
                    coroutineScope.launch {
                        scaffoldState.drawerState.close()
                        onAction(it)
                    }
                }
            }
        }, content = {
            it.calculateBottomPadding()

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
                GitReposListing(state.innerContent) {
                    onAction(ListingAction.NextPageTriggerReached)
                }
                PullRefreshIndicator(
                    refreshing,
                    pullRefreshState,
                    Modifier.align(Alignment.TopCenter)
                )
            }
        })
}

@AppThemePreviews
@Composable
private fun ListingScaffoldPreview() {
    val drawerOpenState = DrawerValue.Closed
    val drawerState = ListingDrawerUIState(
        darkMode = true,
    )
    GitTrendingComposeTheme {
        val scaffoldState = ScaffoldState(DrawerState(drawerOpenState), SnackbarHostState())
        ListingScaffold(
            scaffoldState = scaffoldState,
            state = ListingScaffoldUIState(
                drawerState,
                innerContent = GitReposListingData.Loaded(
                    items = PreviewDataUtils.randomRepos.toImmutableList(),
                    showLoadingAtEnd = true
                )
            ),
            onAction = {})
    }
}

@AppThemePreviews
@Composable
private fun ListingScaffoldWithDrawerPreview() {
    val drawerOpenState = DrawerValue.Open
    val drawerState = ListingDrawerUIState(
        darkMode = true,
    )
    GitTrendingComposeTheme {
        val scaffoldState = ScaffoldState(DrawerState(drawerOpenState), SnackbarHostState())
        ListingScaffold(
            scaffoldState = scaffoldState,
            state = ListingScaffoldUIState(
                drawerState,
                innerContent = GitReposListingData.Initial
            ),
            onAction = {})
    }
}