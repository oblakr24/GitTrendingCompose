package com.rokoblak.gittrendingcompose.data.domain

data class GitRepositoryDetails(
    val id: Long,
    val name: String,
    val desc: String?,
    val authorImgUrl: String?,
    val authorName: String,
    val lang: String?,
    val stars: Long,
)
