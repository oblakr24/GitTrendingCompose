package com.rokoblak.gittrendingcompose.service.api.model

import kotlinx.serialization.Serializable

@Serializable
data class GithubSearchResponse(
    val total_count: Int,
    val items: List<Item>,
) {
    @Serializable
    data class Item(
        val id: Long,
        val owner: Owner,
        val name: String,
        val description: String?,
        val stargazers_count: Long,
        val language: String?,
    ) {
        @Serializable
        data class Owner(
            val id: Long,
            val login: String, // name
            val avatar_url: String?,
        )
    }
}