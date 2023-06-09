package com.rokoblak.gittrendingcompose

import app.cash.turbine.test
import com.rokoblak.gittrendingcompose.data.db.ReposDao
import com.rokoblak.gittrendingcompose.data.db.model.GitRepoEntity
import com.rokoblak.gittrendingcompose.data.domain.GitRepository
import com.rokoblak.gittrendingcompose.data.repo.AppRepositoriesLoadingRepo
import com.rokoblak.gittrendingcompose.data.repo.GitRepositoriesLoadingRepo
import com.rokoblak.gittrendingcompose.data.repo.model.LoadErrorType
import com.rokoblak.gittrendingcompose.service.NetworkMonitor
import com.rokoblak.gittrendingcompose.service.api.GithubApi
import com.rokoblak.gittrendingcompose.service.api.model.GithubRepoOwner
import com.rokoblak.gittrendingcompose.service.api.model.GithubSearchResponse
import com.rokoblak.gittrendingcompose.util.TestCoroutineRule
import com.rokoblak.gittrendingcompose.util.awaitItem
import com.rokoblak.gittrendingcompose.util.completeOnSignal
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.take
import org.junit.Rule
import org.junit.Test
import retrofit2.Response
import java.time.Duration
import java.time.Instant

@OptIn(ExperimentalCoroutinesApi::class)
class GitRepositoriesLoadingRepoTest {

    @ExperimentalCoroutinesApi
    @Rule
    @JvmField
    val coroutineTestRule = TestCoroutineRule(unconfined = true)

    private val mockItem = GithubSearchResponse.Item(
        1L,
        GithubRepoOwner(1L, "login1", null),
        "n1",
        "desc1",
        stargazers_count = 1L,
        language = null
    )

    private val mockDbItem = GitRepoEntity(
        id = 1,
        name = "n1",
        desc = "desc1",
        authorImgUrl = null,
        authorName = "login1",
        lang = null,
        stars = 1,
        pageIdx = 1,
        timestampMs = Instant.now().toEpochMilli(),
        orderIdx = 0,
    )

    @Test
    fun testInitialState() = coroutineTestRule.runTest {
        val dao = object : ReposDao {
            val flow = MutableStateFlow<List<GitRepoEntity>>(emptyList())
            override suspend fun getAll(): List<GitRepoEntity> = flow.value
            override fun getAllFlow(): Flow<List<GitRepoEntity>> = flow
            override suspend fun insertAll(repos: List<GitRepoEntity>) = Unit
            override suspend fun deleteAll() = Unit
        }

        val api: GithubApi = mockk()
        coEvery { api.searchRepositories(any()) } returns Response.success(GithubSearchResponse(total_count = 0, items = emptyList()))

        val networkMonitor = object : NetworkMonitor {
            override val connected: StateFlow<Boolean> = MutableStateFlow(true)
        }

        val repo = AppRepositoriesLoadingRepo(dao = dao, api = api, networkMonitor = networkMonitor, coroutineTestRule.testCoroutineDispatcher)

        repo.loadResults.test {
            val firstResult = awaitItem()
            val expected = GitRepositoriesLoadingRepo.LoadResult.LoadingFirstPage
            assertEquals(expected, firstResult)
        }

    }

    @Test
    fun testLoadDoesNotTriggerIfAvailable() = coroutineTestRule.runTest {
        val dao: ReposDao = mockk()
        val api: GithubApi = mockk()

        val networkMonitor = object : NetworkMonitor {
            override val connected: StateFlow<Boolean> = MutableStateFlow(true)
        }

        // Given that we have some initial data (which is not stale)
        val dbState = listOf(mockDbItem)
        coEvery { dao.getAll() } returns dbState
        coEvery { dao.getAllFlow() } returns flowOf(dbState)

        val repo = AppRepositoriesLoadingRepo(dao = dao, api = api, networkMonitor = networkMonitor, coroutineTestRule.testCoroutineDispatcher)

        repo.loadResults.test {
            val firstResult = awaitItem()

            // Then we expect to get the loaded result without any api call being made
            coVerify(exactly = 1) { dao.getAll() }
            coVerify(exactly = 1) { dao.getAllFlow() }
            coVerify(exactly = 0) { api.searchRepositories(any()) }

            assertEquals(GitRepositoriesLoadingRepo.LoadResult.Loaded(listOf(
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
            ), loadingMore = false), firstResult)
        }
    }

    @Test
    fun testLoadTriggersIfAvailableAndStale() = coroutineTestRule.runTest {
        val dao: ReposDao = mockk()
        val api: GithubApi = mockk()

        val networkMonitor = object : NetworkMonitor {
            override val connected: StateFlow<Boolean> = MutableStateFlow(true)
        }

        // Given that we have some initial data but it is stale
        val staleItem = mockDbItem.copy(timestampMs = Instant.now().minus(Duration.ofDays(1)).toEpochMilli())
        val dbState = listOf(staleItem)
        coEvery { dao.getAll() } returns dbState
        coEvery { dao.getAllFlow() } returns flowOf(dbState)
        coEvery { dao.deleteAll() } returns Unit
        coEvery { dao.insertAll(any()) } returns Unit
        coEvery { api.searchRepositories(any()) } returns Response.success(GithubSearchResponse(total_count = 0, items = listOf(mockItem)))

        val repo = AppRepositoriesLoadingRepo(dao = dao, api = api, networkMonitor = networkMonitor, coroutineTestRule.testCoroutineDispatcher)

        repo.loadResults.test {
            val firstResult = awaitItem()

            // Then we expect that we make an api call to refresh it, and delete the previous data
            coVerify(exactly = 1) { dao.getAll() }
            coVerify(exactly = 1) { dao.getAllFlow() }
            coVerify(exactly = 1) { api.searchRepositories(any()) }
            coVerify(exactly = 1) { dao.deleteAll() }
            coVerify(exactly = 1) { dao.insertAll(any()) }

            assertEquals(
                GitRepositoriesLoadingRepo.LoadResult.Loaded(
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
                    ), loadingMore = false
                ), firstResult
            )
        }
    }

    @Test
    fun testNoNetworkReturnsCorrectData() = coroutineTestRule.runTest {
        val dao: ReposDao = mockk()
        val api: GithubApi = mockk()
        val networkMonitor: NetworkMonitor = mockk()

        // Given that we have no network and no stored data
        val dbFlow = MutableStateFlow<List<GitRepoEntity>>(emptyList())
        every { networkMonitor.connected } returns flowOf(false)
        coEvery { dao.getAll() } returns emptyList()
        coEvery { dao.getAllFlow() } returns dbFlow.take(2)
        coEvery { dao.deleteAll() } returns Unit

        val repo = AppRepositoriesLoadingRepo(dao = dao, api = api, networkMonitor = networkMonitor, coroutineTestRule.testCoroutineDispatcher)

        repo.loadResults.test {
            val firstResult = awaitItem()

            // Then we expect to get an error, without any api call being made
            coVerify(exactly = 1) { dao.getAll() }
            coVerify(exactly = 1) { dao.getAllFlow() }
            coVerify(exactly = 0) { api.searchRepositories(any()) }
            coVerify(exactly = 1) { dao.deleteAll() }
            coVerify(exactly = 0) { dao.insertAll(any()) }

            assertEquals(
                GitRepositoriesLoadingRepo.LoadResult.LoadError(LoadErrorType.NoNetwork),
                firstResult
            )

            // Given that we then get the network back and we reload
            val dbState = listOf(mockDbItem)
            dbFlow.value = dbState
            coEvery { dao.getAll() } returns dbState
            every { networkMonitor.connected } returns MutableStateFlow(true)
            coEvery { dao.insertAll(any()) } returns Unit
            coEvery { api.searchRepositories(any()) } returns Response.success(
                GithubSearchResponse(
                    total_count = 0,
                    items = listOf(mockItem)
                )
            )

            repo.reload()

            // Then we expect to get the loaded result with a call being made and the items inserted
            coVerify(exactly = 1) { api.searchRepositories(any()) }
            coVerify(exactly = 1) { dao.insertAll(any()) }

            val secondResult = awaitItem { it != firstResult }
            assertEquals(
                GitRepositoriesLoadingRepo.LoadResult.Loaded(
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
                    ), loadingMore = false
                ), secondResult
            )

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun testReload() = coroutineTestRule.runTest {
        val stopSignal = MutableStateFlow(false)
        val dao = object : ReposDao {
            val flow = MutableStateFlow<List<GitRepoEntity>>(emptyList())
            override suspend fun getAll(): List<GitRepoEntity> = flow.value
            override fun getAllFlow(): Flow<List<GitRepoEntity>> = flow.completeOnSignal(stopSignal)
            override suspend fun insertAll(repos: List<GitRepoEntity>) {
                flow.value = repos
            }
            override suspend fun deleteAll() {
                flow.value = emptyList()
            }
        }

        val api: GithubApi = mockk()

        val networkMonitor: NetworkMonitor = object : NetworkMonitor {
            override val connected: StateFlow<Boolean> = MutableStateFlow(true)
        }

        val repo = AppRepositoriesLoadingRepo(dao = dao, api = api, networkMonitor = networkMonitor, coroutineTestRule.testCoroutineDispatcher)

        // Given that we have a success response
        coEvery { api.searchRepositories(any()) } returns Response.success(
            GithubSearchResponse(
                total_count = 0,
                items = listOf(mockItem)
            )
        )

        repo.loadResults.test {
            val firstResult = awaitItem()

            // Then we expect to see it returned
            assertEquals(
                GitRepositoriesLoadingRepo.LoadResult.Loaded(
                    listOf(
                        GitRepository(
                            id = 1,
                            name = "n1",
                            desc = "desc1",
                            authorImgUrl = null,
                            authorName = "login1",
                            lang = null,
                            stars = 1,
                            pageIdx = 1
                        )
                    ), false
                ), firstResult
            )

            // Given that a new call would return a different response
            coEvery { api.searchRepositories(any()) } returns Response.success(
                GithubSearchResponse(
                    total_count = 0,
                    items = listOf(mockItem.copy(stargazers_count = 2))
                )
            )

            repo.reload()
            stopSignal.value = true
            val loadingRes = awaitItem { it != firstResult }

            assertEquals(GitRepositoriesLoadingRepo.LoadResult.LoadingFirstPage, loadingRes)

            val nextResult = awaitItem { it != loadingRes }

            // Then we expect to get an updated result returned
            val expectedAfter = GitRepositoriesLoadingRepo.LoadResult.Loaded(
                listOf(
                    GitRepository(
                        id = 1,
                        name = "n1",
                        desc = "desc1",
                        authorImgUrl = null,
                        authorName = "login1",
                        lang = null,
                        stars = 2,
                        pageIdx = 1
                    )
                ), true
            )

            assertEquals(expectedAfter, nextResult)

            cancelAndIgnoreRemainingEvents()
        }
    }
}
