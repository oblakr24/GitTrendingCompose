package com.rokoblak.gittrendingcompose.data.domain

data class ExpandedGitRepositoryDetails(
    val details: GitRepositoryDetails,
    val contents: List<RepoContentFile>,
)

data class RepoContentFile(
    val name: String,
    val type: String,
)
