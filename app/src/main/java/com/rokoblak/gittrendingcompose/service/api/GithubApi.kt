package com.rokoblak.gittrendingcompose.service.api

import com.rokoblak.gittrendingcompose.data.repo.model.CallResult
import com.rokoblak.gittrendingcompose.data.repo.model.LoadErrorType
import com.rokoblak.gittrendingcompose.service.api.model.GitContentFile
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

    @GET("repos/{owner}/{repo}/contents")
    suspend fun getRepoContents(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
    ): Response<List<GitContentFile>>
}

suspend fun <T>GithubApi.wrappedSafeCall(call: suspend GithubApi.() -> Response<T>): CallResult<T> {
    return try {
        val value = call(this)
        value.map {
            it
        }
    } catch (e: Exception) {
        CallResult.Error(LoadErrorType.ApiError(e.message ?: "Api error"))
    }
}

fun <T, R>Response<T>.map(mapper: (T) -> R): CallResult<R> {
    if (!isSuccessful) return CallResult.Error(LoadErrorType.ApiError(this.message()))
    val body = this.body() ?: return CallResult.Error(LoadErrorType.ApiError("Empty body"))
    return CallResult.Success(mapper(body))
}