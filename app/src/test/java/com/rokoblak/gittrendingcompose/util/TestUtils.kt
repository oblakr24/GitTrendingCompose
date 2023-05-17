package com.rokoblak.gittrendingcompose.util

import com.rokoblak.gittrendingcompose.navigation.NavigationState
import com.rokoblak.gittrendingcompose.navigation.RouteNavigator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.transformWhile
import kotlinx.coroutines.withTimeout

object TestUtils {

    val emptyNavigator = object : RouteNavigator {
        override fun onNavigated(state: NavigationState) = Unit

        override fun navigateUp() = Unit

        override fun popToRoute(route: String) = Unit

        override fun navigateToRoute(route: String) = Unit

        override val navigationState: StateFlow<NavigationState> =
            MutableStateFlow(NavigationState.Idle)
    }
}

/**
 * A helper to await a flow emission matching a condition. This is when we're not that interested in the exact sequence of emissions, we just want to await the right one.
 * The timeout works on a test scheduler so it passes near-instantly.
 */
suspend fun <T> Flow<T>.awaitState(condition: (T) -> Boolean): T {
    return withTimeout(100) { // The flow should fill up near-instantly, this is just to fail the test in case there is an assertion failure
        transformWhile { state ->
            emit(state)
            !condition(state)
        }.last()
    }
}