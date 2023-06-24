package com.rokoblak.gittrendingcompose.di

import com.rokoblak.gittrendingcompose.data.repo.AppGitRepoDetailsRepo
import com.rokoblak.gittrendingcompose.data.repo.AppGitReposRepo
import com.rokoblak.gittrendingcompose.data.repo.GitRepoDetailsRepo
import com.rokoblak.gittrendingcompose.data.repo.GitReposRepo
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class ReposModule {

    @Binds
    abstract fun provideDetailsRepo(impl: AppGitRepoDetailsRepo): GitRepoDetailsRepo

    @Binds
    abstract fun provideReposRepo(impl: AppGitReposRepo): GitReposRepo
}
