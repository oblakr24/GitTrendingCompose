package com.rokoblak.gittrendingcompose.di

import com.rokoblak.gittrendingcompose.service.api.GithubApi
import com.rokoblak.gittrendingcompose.service.api.GithubRawFilesApi
import dagger.Module
import dagger.Provides
import dagger.Reusable
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Named

@Module
@InstallIn(SingletonComponent::class)
class ApiModule {

    @Provides
    @Reusable
    fun provideGithubApi(@Named(Names.RETROFIT_DEFAULT) retrofit: Retrofit): GithubApi {
        return retrofit.create(GithubApi::class.java)
    }

    @Provides
    @Reusable
    fun provideGithubRawFilesApi(@Named(Names.RETROFIT_RAW_FILES) retrofit: Retrofit): GithubRawFilesApi {
        return retrofit.create(GithubRawFilesApi::class.java)
    }
}