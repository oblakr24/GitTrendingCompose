package com.rokoblak.gittrendingcompose.data.repo

import com.rokoblak.gittrendingcompose.data.domain.GitRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import javax.inject.Inject

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

// TODO: Implement
class AppRepositoriesLoadingRepo @Inject constructor(

) : GitRepositoriesLoadingRepo {
    override val loadResults: Flow<GitRepositoriesLoadingRepo.LoadResult> = emptyFlow()

    override suspend fun loadNext() = Unit

    override suspend fun reload() = Unit

}