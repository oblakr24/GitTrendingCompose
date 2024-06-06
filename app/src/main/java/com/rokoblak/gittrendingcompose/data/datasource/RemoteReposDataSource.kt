package com.rokoblak.gittrendingcompose.data.datasource

import com.rokoblak.gittrendingcompose.data.model.ReposPage
import com.rokoblak.gittrendingcompose.data.repo.RepoModelMapper.mapToDomain
import com.rokoblak.gittrendingcompose.data.repo.model.CallResult
import com.rokoblak.gittrendingcompose.data.repo.model.LoadErrorType
import com.rokoblak.gittrendingcompose.service.NetworkMonitor
import com.rokoblak.gittrendingcompose.service.api.GithubApi
import com.rokoblak.gittrendingcompose.service.api.wrappedSafeCall
import kotlinx.coroutines.flow.first
import javax.inject.Inject

interface RemoteReposDataSource {
    suspend fun load(page: Int, startIdx: Int): CallResult<ReposPage>

    companion object {
        const val PAGE_START = 1
    }
}

class AppRemoteReposDataSource @Inject constructor(
    private val api: GithubApi,
    private val networkMonitor: NetworkMonitor,
) : RemoteReposDataSource {

    override suspend fun load(page: Int, startIdx: Int): CallResult<ReposPage> {
        if (!networkMonitor.connected.first()) {
            return CallResult.Error(LoadErrorType.NoNetwork)
        }
        return api.wrappedSafeCall {
            searchRepositories(page = page)
        }.map {
            ReposPage(
                repos = it.mapToDomain(page),
                page = page,
                end = it.items.isEmpty(),
                startIdx = startIdx,
            )
        }
    }
}
