package com.rokoblak.gittrendingcompose

import app.cash.turbine.test
import com.rokoblak.gittrendingcompose.data.repo.GitRepositoriesLoadingRepo
import com.rokoblak.gittrendingcompose.data.repo.GitRepositoriesLoadingRepo.*
import com.rokoblak.gittrendingcompose.data.repo.GitRepositoriesLoadingRepo.LoadResult.LoadingFirstPage
import com.rokoblak.gittrendingcompose.service.PersistedStorage
import com.rokoblak.gittrendingcompose.ui.screens.reposlisting.ListingAction
import com.rokoblak.gittrendingcompose.ui.screens.reposlisting.ReposListingViewModel
import com.rokoblak.gittrendingcompose.ui.screens.reposlisting.composables.GitReposListingData
import com.rokoblak.gittrendingcompose.ui.screens.reposlisting.composables.ListingDrawerUIState
import com.rokoblak.gittrendingcompose.ui.screens.reposlisting.composables.ListingScaffoldUIState
import com.rokoblak.gittrendingcompose.util.TestCoroutineRule
import com.rokoblak.gittrendingcompose.util.TestUtils
import com.rokoblak.gittrendingcompose.util.awaitItem
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
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
        val repo = object : GitRepositoriesLoadingRepo {
            override val loadResults: Flow<LoadResult> = flowOf(LoadingFirstPage)

            override suspend fun loadNext() = Unit

            override suspend fun reload() = Unit
        }

        val storage = object : PersistedStorage {
            override fun prefsFlow(): Flow<PersistedStorage.Prefs> = flowOf(PersistedStorage.Prefs(darkMode = true))
            override suspend fun updateDarkMode(enabled: Boolean) = Unit
            override suspend fun clear() = Unit
        }

        val vm = ReposListingViewModel(routeNavigator = TestUtils.emptyNavigator, repo = repo, storage = storage)

        val expected  = ListingScaffoldUIState(ListingDrawerUIState(darkMode = true), GitReposListingData.Initial)
        vm.uiState.test {
            assertEquals(expected, this.awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun testErrorIsRetriable() = coroutineTestRule.runTest {
        val repo = object : GitRepositoriesLoadingRepo {

            val flow = MutableStateFlow<LoadResult?>(null)

            override val loadResults: Flow<LoadResult> = flow {
                emit(LoadingFirstPage)
                delay(50)
                emit(LoadResult.LoadError(LoadErrorType.NO_CONNECTION))
                emitAll(flow.filterNotNull())
            }

            override suspend fun loadNext() = Unit

            override suspend fun reload() {
                flow.value = LoadResult.Loaded(emptyList(), loadingMore = false)
            }
        }

        val storage = object : PersistedStorage {
            override fun prefsFlow(): Flow<PersistedStorage.Prefs> = flowOf(PersistedStorage.Prefs(darkMode = true))
            override suspend fun updateDarkMode(enabled: Boolean) = Unit
            override suspend fun clear() = Unit
        }

        val vm = ReposListingViewModel(routeNavigator = TestUtils.emptyNavigator, repo = repo, storage = storage)

        vm.uiState.test {
            val expectedDrawerState = ListingDrawerUIState(darkMode = true)
            val expected  = ListingScaffoldUIState(expectedDrawerState, GitReposListingData.Initial)
            assertEquals(expected, awaitItem())

            val expectedError  = ListingScaffoldUIState(expectedDrawerState, GitReposListingData.Error(isNoConnection = true))

            assertEquals(expectedError, awaitItem { it.innerContent != GitReposListingData.Initial })

            vm.onAction(ListingAction.RefreshTriggered)

            val expectedAfterRefresh  = ListingScaffoldUIState(expectedDrawerState, GitReposListingData.Loaded(persistentListOf(), false))
            assertEquals(expectedAfterRefresh, awaitItem { it.innerContent is GitReposListingData.Loaded })
        }
    }

    @Test
    fun testDarkModeSwitched() = coroutineTestRule.runTest {
        val repo: GitRepositoriesLoadingRepo = mockk()
        val storage: PersistedStorage = mockk()

        val prefsFlow = MutableStateFlow(PersistedStorage.Prefs(darkMode = null))

        coEvery { storage.prefsFlow() } returns prefsFlow
        coEvery { repo.loadResults } returns flowOf(LoadingFirstPage)

        val vm = ReposListingViewModel(routeNavigator = TestUtils.emptyNavigator, repo = repo, storage = storage)

        vm.uiState.test {

            val expected  = ListingScaffoldUIState(ListingDrawerUIState(darkMode = null), GitReposListingData.Initial)
            val initialState = awaitItem()
            assertEquals(expected, initialState)

            coEvery { storage.updateDarkMode(true) } returns Unit
            prefsFlow.value = PersistedStorage.Prefs(darkMode = true)
            vm.onAction(ListingAction.SetDarkMode(true))

            val updatedState = awaitItem { it != initialState }

            val expectedUpdated  = ListingScaffoldUIState(ListingDrawerUIState(darkMode = true), GitReposListingData.Initial)
            assertEquals(expectedUpdated, updatedState)
        }
    }
}
