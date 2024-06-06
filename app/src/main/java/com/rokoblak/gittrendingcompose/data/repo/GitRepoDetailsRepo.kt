package com.rokoblak.gittrendingcompose.data.repo

import com.rokoblak.gittrendingcompose.data.datasource.RemoteRepoDetailsDataSource
import com.rokoblak.gittrendingcompose.domain.model.ExpandedGitRepositoryDetails
import com.rokoblak.gittrendingcompose.data.model.RepoDetailsInput
import com.rokoblak.gittrendingcompose.data.repo.model.LoadableResult
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import javax.inject.Inject


interface GitRepoDetailsRepo {
    fun loadResults(input: RepoDetailsInput): Flow<LoadableResult<ExpandedGitRepositoryDetails>>
    suspend fun reload()
}

@OptIn(ExperimentalCoroutinesApi::class)
class AppGitRepoDetailsRepo @Inject constructor(
    private val source: RemoteRepoDetailsDataSource,
) : GitRepoDetailsRepo {

    private val inputs = MutableStateFlow<RepoDetailsInput?>(null)
    private var refreshing = MutableStateFlow(RefreshSignal())

    private val loadResults = combine(inputs.filterNotNull(), refreshing) { input, _ ->
        source.load(input)
    }.flatMapLatest { loadFlow ->
        flow {
            emit(LoadableResult.Loading)
            emitAll(loadFlow)
        }
    }

    override fun loadResults(input: RepoDetailsInput): Flow<LoadableResult<ExpandedGitRepositoryDetails>> {
        return loadResults.also {
            inputs.value = input
        }
    }

    override suspend fun reload() {
        refreshing.value = RefreshSignal()
    }

    class RefreshSignal
}
