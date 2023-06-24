package com.rokoblak.gittrendingcompose.domain.model

data class ReposListing(
    val repos: List<GitRepository>,
    val loadingMore: Boolean,
    val page: Int,
    val end: Boolean,
) {
    val lastIdx: Int = repos.size
}