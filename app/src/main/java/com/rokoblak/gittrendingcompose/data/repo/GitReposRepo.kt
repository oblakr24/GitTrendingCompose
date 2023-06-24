package com.rokoblak.gittrendingcompose.data.repo

import com.rokoblak.gittrendingcompose.data.datasource.LocalReposDataSource
import com.rokoblak.gittrendingcompose.data.datasource.RemoteReposDataSource
import com.rokoblak.gittrendingcompose.data.model.ReposPage
import com.rokoblak.gittrendingcompose.data.repo.model.CallResult
import com.rokoblak.gittrendingcompose.data.repo.model.LoadableResult
import com.rokoblak.gittrendingcompose.data.repo.model.toLoadable
import com.rokoblak.gittrendingcompose.di.MainDispatcher
import com.rokoblak.gittrendingcompose.domain.model.ReposListing
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

interface GitReposRepo {
    val flow: Flow<LoadableResult<ReposListing>>
    suspend fun loadNext()
    suspend fun reload()
}

class AppGitReposRepo @Inject constructor(
    private val remote: RemoteReposDataSource,
    private val local: LocalReposDataSource,
    @MainDispatcher private val dispatcher: CoroutineDispatcher,
) : GitReposRepo {

    private val repoScope = CoroutineScope(dispatcher + Job())

    private val persistedItems = flow {
        val initial = local.persistedItems.first()
        if (initial.stale || initial.repos.isEmpty()) reload()
        emitAll(local.persistedItems)
    }

    private val loadingMore = MutableStateFlow(false)
    private val calls = MutableStateFlow<CallResult<ReposPage>?>(null)

    private val results =
        combine(persistedItems, calls, loadingMore) { loaded, callRes, loadingMore ->
            if (loaded.repos.isNotEmpty()) {
                LoadableResult.Success(
                    ReposListing(
                        repos = loaded.repos,
                        loadingMore = loadingMore,
                        page = loaded.repos.last().pageIdx,
                        end = callRes?.optValue?.end
                            ?: false, // Assume end not reached when loading from local source of truth
                    )
                )
            } else {
                when (callRes) {
                    is CallResult.Error -> callRes.toLoadable()
                    else -> LoadableResult.Loading
                }
            }
        }.stateIn(repoScope, SharingStarted.Lazily, initialValue = LoadableResult.Loading)

    override val flow: Flow<LoadableResult<ReposListing>> = results

    override suspend fun loadNext() {
        if (reachedEnd()) return
        if (loading()) return
        loadingMore.value = true
        val lastPage = lastPage()
        makeLoad(page = lastPage?.page?.let { it + 1 } ?: RemoteReposDataSource.PAGE_START,
            startIdx = lastPage?.lastIdx?.let { it + 1 } ?: 0)
    }

    override suspend fun reload() {
        local.clear()
        calls.value = null
        makeLoad(page = RemoteReposDataSource.PAGE_START, startIdx = 0)
    }

    private suspend fun makeLoad(page: Int, startIdx: Int) {
        val res = remote.load(page, startIdx = startIdx)
        when (res) {
            is CallResult.Error -> Unit
            is CallResult.Success -> local.store(res.value)
        }
        loadingMore.value = false
        calls.value = res
    }

    private fun reachedEnd() = lastPage()?.end ?: false
    private fun loading() = results.value is LoadableResult.Loading
    private fun lastPage() = (results.value as? LoadableResult.Success)?.value
}
