package com.rokoblak.gittrendingcompose.data.model

import com.rokoblak.gittrendingcompose.data.domain.GitRepository

data class LoadedRepos(
    val repos: List<GitRepository>,
    val stale: Boolean,
)
