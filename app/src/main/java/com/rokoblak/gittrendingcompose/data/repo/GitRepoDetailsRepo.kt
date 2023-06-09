package com.rokoblak.gittrendingcompose.data.repo

import com.rokoblak.gittrendingcompose.data.domain.ExpandedGitRepositoryDetails
import com.rokoblak.gittrendingcompose.data.repo.model.LoadableResult
import kotlinx.coroutines.flow.Flow


interface GitRepoDetailsRepo {

    fun loadResults(input: Input): Flow<LoadableResult<ExpandedGitRepositoryDetails>>

    suspend fun reload()

    data class Input(
        val owner: String,
        val repo: String,
    )
}
