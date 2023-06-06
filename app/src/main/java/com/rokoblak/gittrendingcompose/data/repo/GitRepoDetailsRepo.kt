package com.rokoblak.gittrendingcompose.data.repo

import com.rokoblak.gittrendingcompose.data.domain.GitRepositoryDetails
import com.rokoblak.gittrendingcompose.data.repo.model.LoadErrorType
import kotlinx.coroutines.flow.Flow


interface GitRepoDetailsRepo {

    fun loadResults(input: Input): Flow<LoadResult>

    suspend fun reload()

    sealed interface LoadResult {
        data class LoadError(val type: LoadErrorType) : LoadResult
        object Loading : LoadResult
        data class Loaded(val repo: GitRepositoryDetails) : LoadResult
    }

    data class Input(
        val owner: String,
        val repo: String,
    )
}
