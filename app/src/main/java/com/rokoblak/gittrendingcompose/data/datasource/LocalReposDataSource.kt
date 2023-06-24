package com.rokoblak.gittrendingcompose.data.datasource

import com.rokoblak.gittrendingcompose.data.db.ReposDao
import com.rokoblak.gittrendingcompose.data.db.model.GitRepoEntity
import com.rokoblak.gittrendingcompose.data.model.LoadedRepos
import com.rokoblak.gittrendingcompose.data.model.ReposPage
import com.rokoblak.gittrendingcompose.data.repo.RepoModelMapper.mapToDomain
import com.rokoblak.gittrendingcompose.data.repo.RepoModelMapper.mapToEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Duration
import java.time.Instant
import javax.inject.Inject

interface LocalReposDataSource {

    val persistedItems: Flow<LoadedRepos>
    suspend fun clear()
    suspend fun store(page: ReposPage)
}

class AppLocalReposDataSource @Inject constructor(
    private val dao: ReposDao,
) : LocalReposDataSource {

    override val persistedItems = dao.getAllFlow().map { entities ->
        val all = entities.map {
            it.mapToDomain()
        }
        LoadedRepos(repos = all, stale = entities.firstOrNull()?.isStale() ?: false)
    }


    override suspend fun store(page: ReposPage) {
        val entities = page.repos.mapIndexed { idx, repo ->
            repo.mapToEntity(orderIdx = page.startIdx + idx)
        }
        if (entities.isNotEmpty()) {
            dao.insertAll(entities)
        }
    }

    override suspend fun clear() {
        dao.deleteAll()
    }

    // If an entity was inserted too long ago, we consider it as stale, meaning that it makes sense to refresh it from the API
    private fun GitRepoEntity.isStale() =
        Instant.ofEpochMilli(timestampMs).isBefore(Instant.now().minus(AGE_TOO_STALE))

    companion object {
        private val AGE_TOO_STALE = Duration.ofMinutes(5)
    }
}