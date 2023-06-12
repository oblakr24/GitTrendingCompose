package com.rokoblak.gittrendingcompose.data.repo

import com.rokoblak.gittrendingcompose.data.domain.ExpandedGitRepositoryDetails
import com.rokoblak.gittrendingcompose.data.repo.RepoModelMapper.mapToDomain
import com.rokoblak.gittrendingcompose.data.repo.model.CallResult
import com.rokoblak.gittrendingcompose.data.repo.model.LoadErrorType
import com.rokoblak.gittrendingcompose.data.repo.model.LoadableResult
import com.rokoblak.gittrendingcompose.data.repo.model.toLoadable
import com.rokoblak.gittrendingcompose.service.NetworkMonitor
import com.rokoblak.gittrendingcompose.service.api.GithubApi
import com.rokoblak.gittrendingcompose.service.api.GithubRawFilesApi
import com.rokoblak.gittrendingcompose.service.api.wrappedSafeCall
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
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
    private val rawFilesApi: GithubRawFilesApi,
    private val networkMonitor: NetworkMonitor,
) : GitRepoDetailsRepo {

    private val inputs = MutableStateFlow<GitRepoDetailsRepo.Input?>(null)
    private var refreshing = MutableStateFlow(RefreshSignal())

    private val loadResults = combine(inputs.filterNotNull(), refreshing) { input, _ ->
        load(input)
    }.flatMapLatest { loadFlow ->
        flow {
            emit(LoadableResult.Loading)
            emitAll(loadFlow)
        }
    }

    override fun loadResults(input: GitRepoDetailsRepo.Input): Flow<LoadableResult<ExpandedGitRepositoryDetails>> {
        return loadResults.also {
            inputs.value = input
        }
    }

    private suspend fun load(input: GitRepoDetailsRepo.Input): Flow<LoadableResult<ExpandedGitRepositoryDetails>> = flow {
        emit(LoadableResult.Loading)
        if (!networkMonitor.connected.first()) {
            emit(LoadableResult.Error(LoadErrorType.NoNetwork))
            return@flow
        }
        emit(loadDetails(owner = input.owner, repo = input.repo).toLoadable())
    }

    override suspend fun reload() {
        refreshing.value = RefreshSignal()
    }

    private suspend fun loadDetails(owner: String, repo: String) = coroutineScope {
        val detailsCall = async {
            api.wrappedSafeCall {
                getRepo(owner = owner, repo = repo)
            }.map {
                it.mapToDomain()
            }
        }
        val contentsCall = async {
            api.wrappedSafeCall {
                getRepoContents(owner = owner, repo = repo)
            }.map {
                it.mapToDomain()
            }
        }
        val detsAndContents = CallResult.compose(detailsCall.await(), contentsCall.await()) { details, contents ->
            details to contents
        }
        val (details, contents) = when (detsAndContents) {
            is CallResult.Error -> return@coroutineScope detsAndContents
            is CallResult.Success -> detsAndContents.value
        }

        val readmeFile =
            contents.firstOrNull { it.name.startsWith("README", ignoreCase = true) }

        val rawReadme = try {
            if (readmeFile?.name != null) {
                val res =
                    rawFilesApi.getRepoFile(owner, repo, branch = details.defaultBranch, filename =readmeFile.name)
                res.body()
            } else {
                null
            }
        } catch (e: Throwable) {
            e.printStackTrace()
            null
        }

        CallResult.Success(
            ExpandedGitRepositoryDetails(
                details = details,
                contents = contents,
                readmeFilename = readmeFile?.name,
                readmeContent = rawReadme,
            )
        )
    }

    class RefreshSignal
}
