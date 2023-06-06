package com.rokoblak.gittrendingcompose.service.api

import com.rokoblak.gittrendingcompose.service.api.model.GithubRepoResponse
import com.rokoblak.gittrendingcompose.service.api.model.GithubSearchResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface GithubApi {
    @GET("search/repositories?q=language=+sort:stars")
    suspend fun searchRepositories(
        @Query("page") page: Int,
    ): Response<GithubSearchResponse>

    @GET("repos/{owner}/{repo}")
    suspend fun getRepo(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
    ): Response<GithubRepoResponse>
}
