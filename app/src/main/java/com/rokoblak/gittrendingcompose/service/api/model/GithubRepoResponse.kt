package com.rokoblak.gittrendingcompose.service.api.model

import kotlinx.serialization.Serializable

@Serializable
data class GithubRepoResponse(
    val id: Long,
    val owner: GithubRepoOwner,
    val name: String,
    val description: String?,
    val stargazers_count: Long,
    val language: String?,
)