package com.rokoblak.gittrendingcompose.data.repo

import com.rokoblak.gittrendingcompose.service.api.model.GithubSearchResponse
import com.rokoblak.gittrendingcompose.data.db.model.GitRepoEntity
import com.rokoblak.gittrendingcompose.data.domain.GitRepository
import com.rokoblak.gittrendingcompose.data.domain.GitRepositoryDetails
import com.rokoblak.gittrendingcompose.service.api.model.GithubRepoResponse
import java.time.Instant

object RepoModelMapper {

    fun GithubSearchResponse.mapToEntity(page: Int, startIdx: Int): List<GitRepoEntity> {
        return items.withIndex().map { (idx, item) ->
            GitRepoEntity(
                id = item.id,
                name = item.name,
                desc = item.description,
                authorImgUrl = item.owner.avatar_url,
                authorName = item.owner.login,
                lang = item.language,
                stars = item.stargazers_count,
                pageIdx = page,
                timestampMs = Instant.now().toEpochMilli(),
                orderIdx = startIdx + idx,
            )
        }
    }

    fun GitRepoEntity.mapToDomain() = GitRepository(
        id = id,
        name = name,
        desc = desc,
        authorName = authorName,
        authorImgUrl = authorImgUrl,
        lang = lang,
        stars = stars,
        pageIdx = pageIdx,
    )

    fun GithubRepoResponse.mapToDomain() = GitRepositoryDetails(
        id = id,
        name = name,
        desc = description,
        authorName = owner.login,
        authorImgUrl = owner.avatar_url,
        lang = language,
        stars = stargazers_count,
    )
}
