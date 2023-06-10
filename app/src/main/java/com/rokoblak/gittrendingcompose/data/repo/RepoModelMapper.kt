package com.rokoblak.gittrendingcompose.data.repo

import com.rokoblak.gittrendingcompose.service.api.model.GithubSearchResponse
import com.rokoblak.gittrendingcompose.data.db.model.GitRepoEntity
import com.rokoblak.gittrendingcompose.data.domain.GitRepository
import com.rokoblak.gittrendingcompose.data.domain.GitRepositoryDetails
import com.rokoblak.gittrendingcompose.data.domain.RepoContentFile
import com.rokoblak.gittrendingcompose.service.api.model.GitContentFile
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
        watchers = watchers_count,
        issues = open_issues_count,
        forks = forks_count,
        licenseName = license?.name,
        defaultBranch = default_branch,
        visibility = visibility,
    )

    fun List<GitContentFile>.mapToDomain() = map { file ->
        RepoContentFile(
            name = file.name,
            type = file.type,
        )
    }
}
