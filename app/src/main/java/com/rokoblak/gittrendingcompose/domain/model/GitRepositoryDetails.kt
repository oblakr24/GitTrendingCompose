package com.rokoblak.gittrendingcompose.domain.model

data class GitRepositoryDetails(
    val id: Long,
    val name: String,
    val desc: String?,
    val authorImgUrl: String?,
    val authorName: String,
    val lang: String?,
    val stars: Long,
    val issues: Long,
    val forks: Long,
    val watchers: Long,
    val licenseName: String?,
    val visibility: String,
    val defaultBranch: String,
)
