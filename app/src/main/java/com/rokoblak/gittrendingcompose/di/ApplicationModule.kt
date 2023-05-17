package com.rokoblak.gittrendingcompose.di

import com.rokoblak.gittrendingcompose.service.AppStorage
import com.rokoblak.gittrendingcompose.service.PersistedStorage
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class ApplicationModule {

    @Binds
    abstract fun providePersistedStorage(impl: AppStorage): PersistedStorage
}