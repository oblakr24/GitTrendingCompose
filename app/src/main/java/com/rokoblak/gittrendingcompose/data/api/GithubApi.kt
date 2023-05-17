package com.rokoblak.gittrendingcompose.data.api

import com.rokoblak.gittrendingcompose.data.api.model.GithubSearchResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface GithubApi {
    @GET("search/repositories?q=language=+sort:stars")
    suspend fun searchRepositories(
        @Query("page") page: Int
    ): Response<GithubSearchResponse>
}
