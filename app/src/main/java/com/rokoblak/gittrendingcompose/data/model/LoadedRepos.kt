package com.rokoblak.gittrendingcompose.data.model

import com.rokoblak.gittrendingcompose.domain.model.GitRepository

data class LoadedRepos(
    val repos: List<GitRepository>,
    val stale: Boolean,
)
