package com.rokoblak.gittrendingcompose.data.repo

import com.rokoblak.gittrendingcompose.data.repo.RepoModelMapper.mapToDomain
import com.rokoblak.gittrendingcompose.data.repo.model.LoadErrorType
import com.rokoblak.gittrendingcompose.service.NetworkMonitor
import com.rokoblak.gittrendingcompose.service.api.GithubApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
class AppGitRepoDetailsRepo @Inject constructor(
    private val api: GithubApi,
    private val networkMonitor: NetworkMonitor,
) : GitRepoDetailsRepo {

    private val inputs = MutableStateFlow<GitRepoDetailsRepo.Input?>(null)
    private var refreshing = MutableStateFlow(RefreshSignal())

    private val loadResults = combine(inputs.filterNotNull(), refreshing) { input, _ ->
        load(input)
    }.flatMapLatest { loadFlow ->
        flow {
            emit(GitRepoDetailsRepo.LoadResult.Loading)
            emitAll(loadFlow)
        }
    }

    override fun loadResults(input: GitRepoDetailsRepo.Input): Flow<GitRepoDetailsRepo.LoadResult> {
        return loadResults.also {
            inputs.value = input
        }
    }

    private suspend fun load(input: GitRepoDetailsRepo.Input): Flow<GitRepoDetailsRepo.LoadResult> = flow {
        emit(GitRepoDetailsRepo.LoadResult.Loading)
        if (!networkMonitor.connected.first()) {
            emit(GitRepoDetailsRepo.LoadResult.LoadError(LoadErrorType.NO_CONNECTION))
            return@flow
        }
        val res = try {
            val resp = api.getRepo(owner = input.owner, repo = input.repo)
            val body = resp.body()
            if (resp.isSuccessful && body != null) {
                val mapped = body.mapToDomain()
                GitRepoDetailsRepo.LoadResult.Loaded(mapped)
            } else {
                GitRepoDetailsRepo.LoadResult.LoadError(LoadErrorType.API_ERROR)
            }
        } catch (e: Throwable) {
            e.printStackTrace()
            GitRepoDetailsRepo.LoadResult.LoadError(LoadErrorType.API_ERROR)
        }
        emit(res)
    }

    override suspend fun reload() {
        refreshing.value = RefreshSignal()
    }

    class RefreshSignal
}
