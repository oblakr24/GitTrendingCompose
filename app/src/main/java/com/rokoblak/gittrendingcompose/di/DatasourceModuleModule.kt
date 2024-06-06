package com.rokoblak.gittrendingcompose.di

import com.rokoblak.gittrendingcompose.data.datasource.AppLocalReposDataSource
import com.rokoblak.gittrendingcompose.data.datasource.AppRemoteRepoDetailsDataSource
import com.rokoblak.gittrendingcompose.data.datasource.AppRemoteReposDataSource
import com.rokoblak.gittrendingcompose.data.datasource.LocalReposDataSource
import com.rokoblak.gittrendingcompose.data.datasource.RemoteRepoDetailsDataSource
import com.rokoblak.gittrendingcompose.data.datasource.RemoteReposDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class DatasourceModuleModule {

    @Binds
    abstract fun provideRemoteReposDataSource(impl: AppRemoteReposDataSource): RemoteReposDataSource

    @Binds
    abstract fun provideLocalReposDataSource(impl: AppLocalReposDataSource): LocalReposDataSource

    @Binds
    abstract fun provideRemoteRepoDetailsDataSource(impl: AppRemoteRepoDetailsDataSource): RemoteRepoDetailsDataSource
}
