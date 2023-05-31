package com.rokoblak.gittrendingcompose.data.repo

import com.rokoblak.gittrendingcompose.data.domain.GitRepository
import kotlinx.coroutines.flow.Flow

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
