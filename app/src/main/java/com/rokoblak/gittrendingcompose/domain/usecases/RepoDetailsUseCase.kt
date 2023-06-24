package com.rokoblak.gittrendingcompose.domain.usecases

import com.rokoblak.gittrendingcompose.domain.model.ExpandedGitRepositoryDetails
import com.rokoblak.gittrendingcompose.data.model.RepoDetailsInput
import com.rokoblak.gittrendingcompose.data.repo.GitRepoDetailsRepo
import com.rokoblak.gittrendingcompose.data.repo.model.LoadableResult
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface RepoDetailsUseCase {
    fun loadResults(input: RepoDetailsInput): Flow<LoadableResult<ExpandedGitRepositoryDetails>>
    suspend fun reload()
}

class AppRepoDetailsUseCase @Inject constructor(
    private val repo: GitRepoDetailsRepo,
) : RepoDetailsUseCase {

    override fun loadResults(input: RepoDetailsInput): Flow<LoadableResult<ExpandedGitRepositoryDetails>> =
        repo.loadResults(input)

    override suspend fun reload() = repo.reload()
}