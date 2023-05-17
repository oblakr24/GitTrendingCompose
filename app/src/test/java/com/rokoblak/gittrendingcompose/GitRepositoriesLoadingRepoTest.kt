package com.rokoblak.gittrendingcompose

import com.rokoblak.gittrendingcompose.data.api.GithubApi
import com.rokoblak.gittrendingcompose.data.api.model.GithubSearchResponse
import com.rokoblak.gittrendingcompose.data.db.ReposDao
import com.rokoblak.gittrendingcompose.data.db.model.GitRepoEntity
import com.rokoblak.gittrendingcompose.data.domain.GitRepository
import com.rokoblak.gittrendingcompose.data.repo.AppRepositoriesLoadingRepo
import com.rokoblak.gittrendingcompose.data.repo.GitRepositoriesLoadingRepo
import com.rokoblak.gittrendingcompose.util.NetworkMonitor
import com.rokoblak.gittrendingcompose.util.TestCoroutineRule
import com.rokoblak.gittrendingcompose.util.awaitState
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
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
        GithubSearchResponse.Item.Owner(1L, "login1", null),
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

        val api = object : GithubApi {
            override suspend fun searchRepositories(page: Int): Response<GithubSearchResponse> {
                return Response.success(GithubSearchResponse(total_count = 0, items = emptyList()))
            }
        }

        val networkMonitor = object : NetworkMonitor {
            override val connected: StateFlow<Boolean> = MutableStateFlow(true)
        }

        val repo = AppRepositoriesLoadingRepo(dao = dao, api = api, networkMonitor = networkMonitor)

        val firstResult = repo.loadResults.first()

        val expected = GitRepositoriesLoadingRepo.LoadResult.LoadingFirstPage

        assertEquals(expected, firstResult)
    }

    @Test
    fun testLoadDoesNotTriggerIfAvailable() = coroutineTestRule.runTest {
        val dao: ReposDao = mockk()
        val api: GithubApi = mockk()

        val networkMonitor = object : NetworkMonitor {
            override val connected: StateFlow<Boolean> = MutableStateFlow(true)
        }

        val dbState = listOf(mockDbItem)
        coEvery { dao.getAll() } returns dbState
        coEvery { dao.getAllFlow() } returns flowOf(dbState)

        val repo = AppRepositoriesLoadingRepo(dao = dao, api = api, networkMonitor = networkMonitor)

        val firstResult = repo.loadResults.first()

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

    @Test
    fun testLoadTriggersIfAvailableAndStale() = coroutineTestRule.runTest {
        val dao: ReposDao = mockk()
        val api: GithubApi = mockk()

        val networkMonitor = object : NetworkMonitor {
            override val connected: StateFlow<Boolean> = MutableStateFlow(true)
        }

        val staleItem = mockDbItem.copy(timestampMs = Instant.now().minus(Duration.ofDays(1)).toEpochMilli())
        val dbState = listOf(staleItem)
        coEvery { dao.getAll() } returns dbState
        coEvery { dao.getAllFlow() } returns flowOf(dbState)
        coEvery { dao.deleteAll() } returns Unit
        coEvery { dao.insertAll(any()) } returns Unit
        coEvery { api.searchRepositories(any()) } returns Response.success(GithubSearchResponse(total_count = 0, items = listOf(mockItem)))

        val repo = AppRepositoriesLoadingRepo(dao = dao, api = api, networkMonitor = networkMonitor)

        val firstResult = repo.loadResults.first()

        coVerify(exactly = 1) { dao.getAll() }
        coVerify(exactly = 1) { dao.getAllFlow() }
        coVerify(exactly = 1) { api.searchRepositories(any()) }
        coVerify(exactly = 1) { dao.deleteAll() }
        coVerify(exactly = 1) { dao.insertAll(any()) }

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

    @Test
    fun testReload() = coroutineTestRule.runTest {
        val dao = object : ReposDao {

            val flow = MutableStateFlow<List<GitRepoEntity>>(emptyList())

            override suspend fun getAll(): List<GitRepoEntity> = flow.value

            override fun getAllFlow(): Flow<List<GitRepoEntity>> = flow

            override suspend fun insertAll(repos: List<GitRepoEntity>) {
                flow.value = repos
            }

            override suspend fun deleteAll() {
                flow.value = emptyList()
            }
        }

        val api: GithubApi = mockk()

        val networkMonitor = object : NetworkMonitor {
            override val connected: StateFlow<Boolean> = MutableStateFlow(true)
        }

        val repo = AppRepositoriesLoadingRepo(dao = dao, api = api, networkMonitor = networkMonitor)

        coEvery { api.searchRepositories(any()) } returns Response.success(
            GithubSearchResponse(
                total_count = 0,
                items = listOf(mockItem)
            )
        )
        val firstResult = repo.loadResults.first()

        val expected = GitRepositoriesLoadingRepo.LoadResult.Loaded(
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
        )

        assertEquals(expected, firstResult)

        coEvery { api.searchRepositories(any()) } returns Response.success(
            GithubSearchResponse(
                total_count = 0,
                items = listOf(mockItem.copy(stargazers_count = 2))
            )
        )

        repo.reload()
        val nextResult = repo.loadResults.awaitState { it != firstResult }

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
            ), false
        )

        assertEquals(expectedAfter, nextResult)
    }
}