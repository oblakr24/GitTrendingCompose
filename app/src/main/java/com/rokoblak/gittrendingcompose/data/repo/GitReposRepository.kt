package com.rokoblak.gittrendingcompose.data.repo

import com.rokoblak.gittrendingcompose.data.db.ReposDao
import com.rokoblak.gittrendingcompose.data.repo.RepoModelMapper.mapToEntity
import com.rokoblak.gittrendingcompose.service.api.GithubApi
import com.rokoblak.gittrendingcompose.util.DataResult
import javax.inject.Inject

class GitReposRepository @Inject constructor(
    private val dao: ReposDao,
    private val api: GithubApi,
) {

    var reachedEnd = false
        private set

    private suspend fun makeLoad(page: Int, startIdx: Int): DataResult<Unit> {
        return try {
            val resp = api.searchRepositories(page = page)
            val body = resp.body()
            if (resp.isSuccessful && body != null) {
                val mapped = body.mapToEntity(page, startIdx)
                if (mapped.isNotEmpty()) {
                    dao.insertAll(mapped)
                } else {
                    reachedEnd = true
                }
                DataResult.Success(Unit)
            } else {
                DataResult.Error("Api error: ${resp.message()}")
            }
        } catch (e: Throwable) {
            e.printStackTrace()

            DataResult.Error("Api error: ${e.message}", e)
        }
    }
}