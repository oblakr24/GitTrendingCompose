package com.rokoblak.gittrendingcompose.domain.model

data class GitRepository(
    val id: Long,
    val name: String,
    val desc: String?,
    val authorImgUrl: String?,
    val authorName: String,
    val lang: String?,
    val stars: Long,
    val pageIdx: Int,
)
