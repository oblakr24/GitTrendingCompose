package com.rokoblak.gittrendingcompose.service.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface GithubRawFilesApi {

    @GET("https://raw.githubusercontent.com/{owner}/{repo}/{branch}/{filename}")
    suspend fun getRepoFile(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("branch") branch: String,
        @Path("filename") filename: String,
    ): Response<String>
}