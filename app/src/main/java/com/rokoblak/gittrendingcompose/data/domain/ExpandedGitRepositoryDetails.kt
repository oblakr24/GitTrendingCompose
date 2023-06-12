package com.rokoblak.gittrendingcompose.data.domain

data class ExpandedGitRepositoryDetails(
    val details: GitRepositoryDetails,
    val contents: List<RepoContentFile>,
    val readmeFilename: String?,
    val readmeContent: String?,
)

data class RepoContentFile(
    val name: String,
    val type: String,
    val downloadUrl: String?,
)
