package com.rokoblak.gittrendingcompose.data.repo

import com.rokoblak.gittrendingcompose.data.db.ReposDao
import com.rokoblak.gittrendingcompose.data.db.model.GitRepoEntity
import com.rokoblak.gittrendingcompose.data.repo.RepoModelMapper.mapToDomain
import com.rokoblak.gittrendingcompose.data.repo.RepoModelMapper.mapToEntity
import com.rokoblak.gittrendingcompose.data.repo.model.LoadErrorType
import com.rokoblak.gittrendingcompose.di.MainDispatcher
import com.rokoblak.gittrendingcompose.service.NetworkMonitor
import com.rokoblak.gittrendingcompose.service.api.GithubApi
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import java.time.Duration
import java.time.Instant
import javax.inject.Inject

/**
 * A loading repository that coordinates between database and network api to perform calls,
 * construct the state and maintain a single source of truth.
 */
class AppRepositoriesLoadingRepo @Inject constructor(
    private val dao: ReposDao,
    private val api: GithubApi,
    private val networkMonitor: NetworkMonitor,
    @MainDispatcher private val dispatcher: CoroutineDispatcher,
) : GitRepositoriesLoadingRepo {

    private val repoScope = CoroutineScope(dispatcher + Job())

    private val errored = MutableStateFlow<LoadErrorType?>(null)
    private val loading = MutableStateFlow(false)
    private var reachedEnd = false

    // Any time we collect from the current DB state, we are going to check whether we need to do the initial (re)load
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
            !connected && entities.isEmpty() -> GitRepositoriesLoadingRepo.LoadResult.LoadError(
                LoadErrorType.NoNetwork
            )
            error != null -> GitRepositoriesLoadingRepo.LoadResult.LoadError(error)
            entities.isNotEmpty() -> GitRepositoriesLoadingRepo.LoadResult.Loaded(
                loadedItems = entities.map { it.mapToDomain() },
                loadingMore = !reachedEnd && loading,
            )

            else -> GitRepositoriesLoadingRepo.LoadResult.LoadingFirstPage
        }
    }

    override suspend fun loadNext() {
        if (reachedEnd) return
        if (loading.value) return
        val lastItem = dbItems.value.lastOrNull()
        val lastLoadedPage = lastItem?.pageIdx
        // We take the last item's order index so that the following items are ordered after it
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
        if (!networkMonitor.connected.first()) {
            errored.value = LoadErrorType.NoNetwork
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
                errored.value = LoadErrorType.ApiError("Body is null")
            }
        } catch (e: Throwable) {
            e.printStackTrace()
            errored.value = LoadErrorType.ApiError(e.message ?: "Api error")
        }
        loading.value = false
    }

    // If an entity was inserted too long ago, we consider it as stale, meaning that it makes sense to refresh it from the API
    private fun GitRepoEntity.isStale() = Instant.ofEpochMilli(timestampMs).isBefore(Instant.now().minus(AGE_TOO_STALE))

    companion object {
        private const val PAGE_START = 1
        private val AGE_TOO_STALE = Duration.ofMinutes(5)
    }
}
