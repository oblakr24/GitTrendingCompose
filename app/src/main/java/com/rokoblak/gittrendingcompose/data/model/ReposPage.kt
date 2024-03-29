package com.rokoblak.gittrendingcompose.data.model

import com.rokoblak.gittrendingcompose.domain.model.GitRepository

data class ReposPage(
    val repos: List<GitRepository>,
    val page: Int,
    val startIdx: Int,
    val end: Boolean,
)
