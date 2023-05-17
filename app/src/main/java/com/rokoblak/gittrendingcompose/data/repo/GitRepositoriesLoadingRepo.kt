package com.rokoblak.gittrendingcompose.data.repo

import com.rokoblak.gittrendingcompose.data.domain.GitRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
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
    override val loadResults: Flow<GitRepositoriesLoadingRepo.LoadResult> = flowOf(
        GitRepositoriesLoadingRepo.LoadResult.Loaded(
            loadedItems = (0..20).map {
                GitRepository(
                    id = it.toLong(),
                    name = "name $it",
                    desc = "desc $it",
                    authorImgUrl = null,
                    authorName = "Author $it",
                    lang = "lang $it",
                    stars = it.toLong(),
                    pageIdx = 1,
                )
            },
            loadingMore = false,
        )
    )

    override suspend fun loadNext() = Unit

    override suspend fun reload() = Unit

}