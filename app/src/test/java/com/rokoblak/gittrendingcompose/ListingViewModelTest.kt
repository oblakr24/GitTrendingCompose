package com.rokoblak.gittrendingcompose

import app.cash.turbine.test
import com.rokoblak.gittrendingcompose.data.repo.model.LoadErrorType
import com.rokoblak.gittrendingcompose.data.repo.model.LoadableResult
import com.rokoblak.gittrendingcompose.domain.model.ReposListing
import com.rokoblak.gittrendingcompose.domain.usecases.DarkModeHandlingUseCase
import com.rokoblak.gittrendingcompose.domain.usecases.ReposListingUseCase
import com.rokoblak.gittrendingcompose.ui.screens.reposlisting.ListingAction
import com.rokoblak.gittrendingcompose.ui.screens.reposlisting.ReposListingViewModel
import com.rokoblak.gittrendingcompose.ui.screens.reposlisting.composables.GitReposListingData
import com.rokoblak.gittrendingcompose.ui.screens.reposlisting.composables.ListingDrawerUIState
import com.rokoblak.gittrendingcompose.ui.screens.reposlisting.composables.ListingScaffoldUIState
import com.rokoblak.gittrendingcompose.util.TestCoroutineRule
import com.rokoblak.gittrendingcompose.util.TestUtils
import com.rokoblak.gittrendingcompose.util.awaitItem
import io.mockk.coEvery
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import junit.framework.TestCase.assertEquals
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ListingViewModelTest {

    @ExperimentalCoroutinesApi
    @Rule
    @JvmField
    val coroutineTestRule = TestCoroutineRule(unconfined = true)

    @Test
    fun testInitialState() = coroutineTestRule.runTest {
        val listingUseCase: ReposListingUseCase = mockk()
        every { listingUseCase.flow } returns flowOf(LoadableResult.Loading)
        val darkModeUseCase: DarkModeHandlingUseCase = mockk()
        every { darkModeUseCase.darkModeEnabled() } returns flowOf(true)

        val vm = ReposListingViewModel(
            routeNavigator = TestUtils.emptyNavigator,
            listingUseCase = listingUseCase,
            darkModeUseCase = darkModeUseCase
        )

        val expected = ListingScaffoldUIState(
            ListingDrawerUIState(darkMode = true),
            GitReposListingData.Initial
        )
        vm.uiState.test {
            assertEquals(expected, this.awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun testErrorIsRetriable() = coroutineTestRule.runTest {
        val reposFlow = MutableStateFlow<LoadableResult<ReposListing>?>(null)
        val listingUseCase: ReposListingUseCase = mockk()
        every { listingUseCase.flow } returns flow {
            emit(LoadableResult.Loading)
            delay(50)
            emit(LoadableResult.Error(LoadErrorType.NoNetwork))
            emitAll(reposFlow.filterNotNull())
        }
        coEvery { listingUseCase.reload() } returns kotlin.run {
            reposFlow.value = LoadableResult.Success(ReposListing(emptyList(), false, 1, false))
        }
        val darkModeUseCase: DarkModeHandlingUseCase = mockk()
        every { darkModeUseCase.darkModeEnabled() } returns flowOf(true)

        val vm = ReposListingViewModel(
            routeNavigator = TestUtils.emptyNavigator,
            listingUseCase = listingUseCase,
            darkModeUseCase = darkModeUseCase
        )

        vm.uiState.test {
            val expectedDrawerState = ListingDrawerUIState(darkMode = true)
            val expected = ListingScaffoldUIState(expectedDrawerState, GitReposListingData.Initial)
            assertEquals(expected, awaitItem())

            val expectedError = ListingScaffoldUIState(
                expectedDrawerState,
                GitReposListingData.Error(isNoConnection = true)
            )
            assertEquals(
                expectedError,
                awaitItem { it.innerContent != GitReposListingData.Initial })

            vm.onAction(ListingAction.RefreshTriggered)

            val expectedAfterRefresh = ListingScaffoldUIState(
                expectedDrawerState,
                GitReposListingData.Loaded(persistentListOf(), false)
            )
            assertEquals(
                expectedAfterRefresh,
                awaitItem { it.innerContent is GitReposListingData.Loaded })
        }
    }

    @Test
    fun testDarkModeSwitched() = coroutineTestRule.runTest {
        val listingUseCase: ReposListingUseCase = mockk()
        every { listingUseCase.flow } returns flowOf(LoadableResult.Loading)

        val darkModeFlow = MutableStateFlow<Boolean?>(null)
        val darkModeUseCase: DarkModeHandlingUseCase = mockk()
        every { darkModeUseCase.darkModeEnabled() } returns darkModeFlow

        val vm = ReposListingViewModel(
            routeNavigator = TestUtils.emptyNavigator,
            listingUseCase = listingUseCase,
            darkModeUseCase = darkModeUseCase
        )

        vm.uiState.test {
            val expected = ListingScaffoldUIState(
                ListingDrawerUIState(darkMode = null),
                GitReposListingData.Initial
            )
            val initialState = awaitItem()
            assertEquals(expected, initialState)

            coEvery { darkModeUseCase.updateDarkMode(true) } just runs
            darkModeFlow.value = true
            vm.onAction(ListingAction.SetDarkMode(true))

            val updatedState = awaitItem { it != initialState }

            val expectedUpdated = ListingScaffoldUIState(
                ListingDrawerUIState(darkMode = true),
                GitReposListingData.Initial
            )
            assertEquals(expectedUpdated, updatedState)
        }
    }
}
