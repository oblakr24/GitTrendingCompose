package com.rokoblak.gittrendingcompose.service.api.model

import kotlinx.serialization.Serializable

@Serializable
data class GithubRepoOwner(
    val id: Long,
    val login: String, // name
    val avatar_url: String?,
)
