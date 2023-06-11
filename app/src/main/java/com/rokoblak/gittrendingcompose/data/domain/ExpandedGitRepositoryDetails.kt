package com.rokoblak.gittrendingcompose.data.domain

data class ExpandedGitRepositoryDetails(
    val details: GitRepositoryDetails,
    val contents: List<RepoContentFile>,
    val rawReadme: String?,
)

data class RepoContentFile(
    val name: String,
    val type: String,
)
