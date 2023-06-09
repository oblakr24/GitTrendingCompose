package com.rokoblak.gittrendingcompose.service.api.model

import kotlinx.serialization.Serializable

@Serializable
data class GithubRepoContentsResponse(
    
)

@Serializable
data class GitContentFile(
    val name: String,
    val path: String,
    val download_url: String,
    val type: String,
)