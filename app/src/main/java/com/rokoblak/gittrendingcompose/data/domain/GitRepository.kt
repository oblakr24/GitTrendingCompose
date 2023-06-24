package com.rokoblak.gittrendingcompose.data.domain

data class GitRepository(
    val id: Long,
    val name: String,
    val desc: String?,
    val authorImgUrl: String?,
    val authorName: String,
    val lang: String?,
    val stars: Long,
    val pageIdx: Int,
) {
    companion object {
        const val PAGE_START = 1
    }
}
