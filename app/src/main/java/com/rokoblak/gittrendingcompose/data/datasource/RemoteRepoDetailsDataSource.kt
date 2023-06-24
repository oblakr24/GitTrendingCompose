package com.rokoblak.gittrendingcompose.data.datasource

import com.rokoblak.gittrendingcompose.domain.model.ExpandedGitRepositoryDetails
import com.rokoblak.gittrendingcompose.domain.model.GitRepositoryDetails
import com.rokoblak.gittrendingcompose.domain.model.RepoContentFile
import com.rokoblak.gittrendingcompose.data.model.RepoDetailsInput
import com.rokoblak.gittrendingcompose.data.repo.RepoModelMapper.mapToDomain
import com.rokoblak.gittrendingcompose.data.repo.model.CallResult
import com.rokoblak.gittrendingcompose.data.repo.model.LoadErrorType
import com.rokoblak.gittrendingcompose.data.repo.model.LoadableResult
import com.rokoblak.gittrendingcompose.data.repo.model.toLoadable
import com.rokoblak.gittrendingcompose.service.NetworkMonitor
import com.rokoblak.gittrendingcompose.service.api.GithubApi
import com.rokoblak.gittrendingcompose.service.api.GithubRawFilesApi
import com.rokoblak.gittrendingcompose.service.api.wrappedSafeCall
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

interface RemoteRepoDetailsDataSource {
    suspend fun load(input: RepoDetailsInput): Flow<LoadableResult<ExpandedGitRepositoryDetails>>
}

class AppRemoteRepoDetailsDataSource @Inject constructor(
    private val api: GithubApi,
    private val rawFilesApi: GithubRawFilesApi,
    private val networkMonitor: NetworkMonitor,
) : RemoteRepoDetailsDataSource {

    override suspend fun load(input: RepoDetailsInput): Flow<LoadableResult<ExpandedGitRepositoryDetails>> =
        flow {
            emit(LoadableResult.Loading)
            if (!networkMonitor.connected.first()) {
                emit(LoadableResult.Error(LoadErrorType.NoNetwork))
                return@flow
            }
            emit(loadDetails(owner = input.owner, repo = input.repo).toLoadable())
        }

    private suspend fun loadDetails(owner: String, repo: String) = coroutineScope {
        val (details, contents) = when (val res = loadContentsAndDetails(owner, repo)) {
            is CallResult.Error -> return@coroutineScope res
            is CallResult.Success -> res.value
        }
        val readmeFile = contents.findReadme()
        val rawReadme = try {
            if (readmeFile?.name != null) {
                rawFilesApi.getRepoFile(
                    owner = owner,
                    repo = repo,
                    branch = details.defaultBranch,
                    filename = readmeFile.name
                ).body()
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

    private fun List<RepoContentFile>.findReadme(): RepoContentFile? {
        val allReadmes = filter { it.name.startsWith("README", ignoreCase = true) }
        return allReadmes.firstOrNull { it.name.startsWith("README.", ignoreCase = true) }
            ?: allReadmes.firstOrNull() // take the english one if there are several
    }

    private suspend fun loadContentsAndDetails(
        owner: String,
        repo: String
    ): CallResult<Pair<GitRepositoryDetails, List<RepoContentFile>>> = coroutineScope {
        val detailsCall = async {
            api.wrappedSafeCall {
                getRepo(owner = owner, repo = repo)
            }.map { it.mapToDomain() }
        }
        val contentsCall = async {
            api.wrappedSafeCall { getRepoContents(owner = owner, repo = repo) }
                .map { it.mapToDomain() }
        }
        CallResult.compose(detailsCall.await(), contentsCall.await()) { details, contents ->
            details to contents
        }
    }
}
