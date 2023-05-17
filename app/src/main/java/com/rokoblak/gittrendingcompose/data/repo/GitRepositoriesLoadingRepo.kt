package com.rokoblak.gittrendingcompose.data.repo

import com.rokoblak.gittrendingcompose.data.api.GithubApi
import com.rokoblak.gittrendingcompose.data.db.ReposDao
import com.rokoblak.gittrendingcompose.data.db.model.GitRepoEntity
import com.rokoblak.gittrendingcompose.data.domain.GitRepository
import com.rokoblak.gittrendingcompose.data.repo.GitRepositoriesLoadingRepo.LoadErrorType
import com.rokoblak.gittrendingcompose.data.repo.GitRepositoriesLoadingRepo.LoadResult
import com.rokoblak.gittrendingcompose.data.repo.RepoModelMapper.mapToDomain
import com.rokoblak.gittrendingcompose.data.repo.RepoModelMapper.mapToEntity
import com.rokoblak.gittrendingcompose.util.NetworkMonitor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import java.time.Duration
import java.time.Instant
import javax.inject.Inject

interface GitRepositoriesLoadingRepo {
    val loadResults: Flow<LoadResult>

    suspend fun loadNext()

    suspend fun reload()

    sealed interface LoadResult {
        data class LoadError(val type: LoadErrorType) : LoadResult
        object LoadingFirstPage : LoadResult
        data class Loaded(
            val loadedItems: List<GitRepository>,
            val loadingMore: Boolean,
        ) : LoadResult
    }

    enum class LoadErrorType { API_ERROR, NO_CONNECTION }
}

class AppRepositoriesLoadingRepo @Inject constructor(
    private val dao: ReposDao,
    private val api: GithubApi,
    private val networkMonitor: NetworkMonitor,
) : GitRepositoriesLoadingRepo {

    private val repoScope = CoroutineScope(Dispatchers.Main + Job())

    private val errored = MutableStateFlow<LoadErrorType?>(null)
    private val loading = MutableStateFlow(false)
    private var reachedEnd = false

    private val persistedItems = flow {
        val initial = dao.getAll()
        if (initial.isEmpty()) {
            reload()
        } else if (initial.first().isStale()) {
            // Here we could add some extra logic to not delete before reloading, just so that in case of no-network, we still have stale data to show.
            reload()
        }
        emitAll(dao.getAllFlow())
    }

    private val dbItems =
        persistedItems.stateIn(repoScope, SharingStarted.Lazily, initialValue = emptyList())

    override val loadResults = combine(
        dbItems,
        networkMonitor.connected,
        errored,
        loading
    ) { entities, connected, error, loading ->
        when {
            !connected && entities.isEmpty() -> LoadResult.LoadError(LoadErrorType.NO_CONNECTION)
            error != null -> LoadResult.LoadError(error)
            entities.isNotEmpty() -> LoadResult.Loaded(
                loadedItems = entities.map { it.mapToDomain() },
                loadingMore = !reachedEnd && loading,
            )

            else -> LoadResult.LoadingFirstPage
        }
    }

    override suspend fun loadNext() {
        if (reachedEnd) return
        if (loading.value) return
        val lastItem = dbItems.value.lastOrNull()
        val lastLoadedPage = lastItem?.pageIdx
        val startIdx = lastItem?.orderIdx?.let { it + 1 }
        makeLoad(lastLoadedPage?.let { it + 1 } ?: PAGE_START, startIdx ?: 0)
    }

    override suspend fun reload() {
        errored.value = null
        reachedEnd = false
        dao.deleteAll()
        makeLoad(page = PAGE_START, startIdx = 0)
    }

    private suspend fun makeLoad(page: Int, startIdx: Int) {
        if (!networkMonitor.connected.value) {
            errored.value = LoadErrorType.NO_CONNECTION
            return
        }
        loading.value = true
        try {
            val resp = api.searchRepositories(page = page)
            val body = resp.body()
            if (resp.isSuccessful && body != null) {
                val mapped = body.mapToEntity(page, startIdx)
                if (mapped.isNotEmpty()) {
                    dao.insertAll(mapped)
                } else {
                    reachedEnd = true
                }
            } else {
                errored.value = LoadErrorType.API_ERROR
            }
        } catch (e: Throwable) {
            e.printStackTrace()
            errored.value = LoadErrorType.API_ERROR
        }
        loading.value = false
    }

    private fun GitRepoEntity.isStale() = Instant.ofEpochMilli(timestampMs).isBefore(Instant.now().minus(DURATION_TOO_STALE))

    companion object {
        private const val PAGE_START = 1
        private val DURATION_TOO_STALE = Duration.ofMinutes(5)
    }
}