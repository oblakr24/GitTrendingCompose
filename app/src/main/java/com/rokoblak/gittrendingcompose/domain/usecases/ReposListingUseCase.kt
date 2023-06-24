package com.rokoblak.gittrendingcompose.domain.usecases

import com.rokoblak.gittrendingcompose.data.repo.GitReposRepo
import com.rokoblak.gittrendingcompose.data.repo.model.LoadableResult
import com.rokoblak.gittrendingcompose.domain.model.ReposListing
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface ReposListingUseCase {

    val flow: Flow<LoadableResult<ReposListing>>

    suspend fun loadNext()

    suspend fun reload()
}

class AppReposListingUseCase @Inject constructor(
    private val repo: GitReposRepo,
) : ReposListingUseCase {

    override val flow: Flow<LoadableResult<ReposListing>> = repo.flow

    override suspend fun loadNext() = repo.loadNext()

    override suspend fun reload() = repo.reload()
}
