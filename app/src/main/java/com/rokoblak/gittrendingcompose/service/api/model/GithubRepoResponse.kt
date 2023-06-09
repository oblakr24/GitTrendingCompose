package com.rokoblak.gittrendingcompose.service.api.model

import kotlinx.serialization.Serializable

@Serializable
data class GithubRepoResponse(
    val id: Long,
    val owner: GithubRepoOwner,
    val name: String,
    val description: String?,
    val stargazers_count: Long,
    val watchers_count: Long,
    val language: String?,
    val private: Boolean,
    val homepage: String?,
    val forks_count: Long,
    val archived: Boolean,
    val disabled: Boolean,
    val open_issues_count: Long,
    val license: GitRepoLicense?,
    val visibility: String,
    val default_branch: String,
    val organization: GitOrganization?,
)

@Serializable
data class GitRepoLicense(
    val key: String,
    val name: String,
    val url: String?,
)

@Serializable
data class GitOrganization(
    val id: Long,
    val login: String,
    val url: String?,
)
