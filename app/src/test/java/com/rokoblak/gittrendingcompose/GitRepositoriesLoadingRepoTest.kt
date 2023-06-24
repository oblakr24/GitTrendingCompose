package com.rokoblak.gittrendingcompose

import app.cash.turbine.test
import com.rokoblak.gittrendingcompose.data.datasource.LocalReposDataSource
import com.rokoblak.gittrendingcompose.data.datasource.RemoteReposDataSource
import com.rokoblak.gittrendingcompose.data.domain.GitRepository
import com.rokoblak.gittrendingcompose.data.model.LoadedRepos
import com.rokoblak.gittrendingcompose.data.model.ReposPage
import com.rokoblak.gittrendingcompose.data.repo.AppGitReposRepo
import com.rokoblak.gittrendingcompose.data.repo.model.CallResult
import com.rokoblak.gittrendingcompose.data.repo.model.LoadErrorType
import com.rokoblak.gittrendingcompose.data.repo.model.LoadableResult
import com.rokoblak.gittrendingcompose.domain.model.ReposListing
import com.rokoblak.gittrendingcompose.util.TestCoroutineRule
import com.rokoblak.gittrendingcompose.util.awaitItem
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.take
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GitRepositoriesLoadingRepoTest {

    @ExperimentalCoroutinesApi
    @Rule
    @JvmField
    val coroutineTestRule = TestCoroutineRule(unconfined = true)

    private val mockItem = GitRepository(
        id = 1,
        name = "n1",
        desc = "desc1",
        authorImgUrl = null,
        authorName = "login1",
        lang = null,
        stars = 1,
        pageIdx = 1,
    )

    @Test
    fun testInitialState() = coroutineTestRule.runTest {
        val local: LocalReposDataSource = mockk()
        every { local.persistedItems } returns flowOf(LoadedRepos(emptyList(), false))
        coEvery { local.clear() } just runs
        coEvery { local.store(any()) } just runs

        val remote: RemoteReposDataSource = mockk()
        coEvery { remote.load(any(), any()) } returns CallResult.Success(
            ReposPage(emptyList(), 0, 0, true)
        )

        val repo = AppGitReposRepo(
            remote = remote,
            local = local,
            dispatcher = coroutineTestRule.testCoroutineDispatcher
        )
        repo.flow.test {
            val firstResult = awaitItem()
            assertEquals(LoadableResult.Loading, firstResult)
        }
    }

    @Test
    fun testLoadDoesNotTriggerIfAvailable() = coroutineTestRule.runTest {
        val local: LocalReposDataSource = mockk()
        val remote: RemoteReposDataSource = mockk()
        // Given that we have some initial data (which is not stale)
        val dbState = listOf(mockItem)
        every { local.persistedItems } returns flowOf(LoadedRepos(dbState, false))

        val repo = AppGitReposRepo(
            remote = remote,
            local = local,
            dispatcher = coroutineTestRule.testCoroutineDispatcher
        )
        repo.flow.test {
            val firstResult = awaitItem()
            // Then we expect to get the loaded result without any api call being made
            coVerify { local.persistedItems }
            coVerify(exactly = 0) { remote.load(any(), any()) }

            val expectedListing = ReposListing(
                repos = listOf(
                    GitRepository(
                        id = 1,
                        name = "n1",
                        desc = "desc1",
                        authorImgUrl = null,
                        authorName = "login1",
                        lang = null,
                        stars = 1,
                        pageIdx = 1,
                    )
                ),
                loadingMore = false, page = 1, end = false
            )
            assertEquals(LoadableResult.Success(expectedListing), firstResult)
        }
    }

    @Test
    fun testLoadTriggersIfAvailableAndStale() = coroutineTestRule.runTest {
        val local: LocalReposDataSource = mockk()
        val remote: RemoteReposDataSource = mockk()
        // Given that we have some initial data but it is stale
        val dbState = listOf(mockItem)
        every { local.persistedItems } returns flowOf(LoadedRepos(dbState, stale = true))
        coEvery { local.clear() } just runs
        coEvery { local.store(any()) } just runs
        coEvery { remote.load(any(), any()) } returns CallResult.Success(
            ReposPage(listOf(mockItem), 1, 0, false)
        )

        val repo = AppGitReposRepo(
            remote = remote,
            local = local,
            dispatcher = coroutineTestRule.testCoroutineDispatcher
        )
        repo.flow.test {
            val firstResult = awaitItem()
            // Then we expect that we make an api call to refresh it, and delete the previous data
            coVerify { local.persistedItems }
            coVerify(exactly = 1) { remote.load(any(), any()) }
            coVerify(exactly = 1) { local.clear() }
            coVerify(exactly = 1) { local.store(any()) }

            val expectedListing = ReposListing(
                repos = listOf(
                    GitRepository(
                        id = 1,
                        name = "n1",
                        desc = "desc1",
                        authorImgUrl = null,
                        authorName = "login1",
                        lang = null,
                        stars = 1,
                        pageIdx = 1,
                    )
                ),
                loadingMore = false, page = 1, end = false
            )
            assertEquals(LoadableResult.Success(expectedListing), firstResult)
        }
    }

    @Test
    fun testNoNetworkReturnsCorrectData() = coroutineTestRule.runTest {
        val local: LocalReposDataSource = mockk()
        val remote: RemoteReposDataSource = mockk()
        // Given that we have no network and no stored data
        val dbFlow = MutableStateFlow(LoadedRepos(emptyList(), stale = false))
        coEvery { local.clear() } just runs
        coEvery { remote.load(any(), any()) } returns CallResult.Error(LoadErrorType.NoNetwork)
        every { local.persistedItems } returns dbFlow.take(2)

        val repo = AppGitReposRepo(
            remote = remote,
            local = local,
            dispatcher = coroutineTestRule.testCoroutineDispatcher
        )

        repo.flow.test {
            val firstResult = awaitItem()

            // Then we expect to get an error, without any api call being made
            coVerify { local.persistedItems }
            coVerify(exactly = 1) { remote.load(any(), any()) }
            coVerify(exactly = 1) { local.clear() }
            coVerify(exactly = 0) { local.store(any()) }

            assertEquals(LoadableResult.Error(LoadErrorType.NoNetwork), firstResult)
            // Given that we then get the network back and we reload
            val dbState = listOf(mockItem)
            dbFlow.value = LoadedRepos(dbState, false)
            coEvery { local.store(any()) } just runs
            coEvery {
                remote.load(
                    any(),
                    any()
                )
            } returns CallResult.Success(
                ReposPage(
                    repos = listOf(mockItem),
                    page = 1,
                    end = false,
                    startIdx = 0
                )
            )

            repo.reload()

            // Then we expect to get the loaded result with the second call being made and the items inserted
            coVerify(exactly = 2) { remote.load(any(), any()) }
            coVerify(exactly = 1) { local.store(any()) }

            val secondResult = this.awaitItem { it != firstResult }
            assertEquals(
                LoadableResult.Success(
                    ReposListing(
                        listOf(
                            GitRepository(
                                id = 1,
                                name = "n1",
                                desc = "desc1",
                                authorImgUrl = null,
                                authorName = "login1",
                                lang = null,
                                stars = 1,
                                pageIdx = 1,
                            )
                        ), loadingMore = false, page = 1, end = false
                    )
                ), secondResult
            )
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun testReload() = coroutineTestRule.runTest {
        val local: LocalReposDataSource = mockk()
        val remote: RemoteReposDataSource = mockk()

        val dbFlow = MutableStateFlow(LoadedRepos(emptyList(), stale = false))
        coEvery { local.clear() } just runs
        coEvery { local.store(any()) } just runs
        every { local.persistedItems } returns dbFlow

        val repo = AppGitReposRepo(
            remote = remote,
            local = local,
            dispatcher = coroutineTestRule.testCoroutineDispatcher
        )

        // Given that we have a success response
        coEvery { remote.load(any(), any()) } returns CallResult.Success(
            ReposPage(listOf(mockItem), 1, 0, false)
        )
        dbFlow.value = LoadedRepos(listOf(mockItem), stale = false)

        repo.flow.test {
            val firstResult = awaitItem()

            // Then we expect to see it returned
            assertEquals(
                LoadableResult.Success(
                    ReposListing(
                        listOf(
                            GitRepository(
                                id = 1,
                                name = "n1",
                                desc = "desc1",
                                authorImgUrl = null,
                                authorName = "login1",
                                lang = null,
                                stars = 1,
                                pageIdx = 1,
                            )
                        ), loadingMore = false, page = 1, end = false
                    )
                ), firstResult
            )

            // Given that a new call would return a different response
            coEvery { remote.load(any(), any()) } returns CallResult.Success(
                ReposPage(listOf(mockItem.copy(stars = 2)), 1, 0, false)
            )

            repo.reload()
            dbFlow.value = LoadedRepos(emptyList(), stale = false)

            val loadingRes = awaitItem { it != firstResult }
            dbFlow.value = LoadedRepos(listOf(mockItem.copy(stars = 2)), stale = false)

            assertEquals(LoadableResult.Loading, loadingRes)

            val nextResult = awaitItem { it != loadingRes }

            // Then we expect to get an updated result returned
            assertEquals(
                LoadableResult.Success(
                    ReposListing(
                        listOf(
                            GitRepository(
                                id = 1,
                                name = "n1",
                                desc = "desc1",
                                authorImgUrl = null,
                                authorName = "login1",
                                lang = null,
                                stars = 2,
                                pageIdx = 1,
                            )
                        ), loadingMore = false, page = 1, end = false
                    )
                ), nextResult
            )

            cancelAndIgnoreRemainingEvents()
        }
    }
}
