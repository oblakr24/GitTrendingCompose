package com.rokoblak.gittrendingcompose.di

import com.rokoblak.gittrendingcompose.domain.usecases.AppDarkModeHandlingUseCase
import com.rokoblak.gittrendingcompose.domain.usecases.AppRepoDetailsUseCase
import com.rokoblak.gittrendingcompose.domain.usecases.AppReposListingUseCase
import com.rokoblak.gittrendingcompose.domain.usecases.DarkModeHandlingUseCase
import com.rokoblak.gittrendingcompose.domain.usecases.RepoDetailsUseCase
import com.rokoblak.gittrendingcompose.domain.usecases.ReposListingUseCase
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class UseCasesModule {

    @Binds
    abstract fun provideDarkModeHandlingUseCase(impl: AppDarkModeHandlingUseCase): DarkModeHandlingUseCase

    @Binds
    abstract fun provideReposListingUseCase(impl: AppReposListingUseCase): ReposListingUseCase

    @Binds
    abstract fun provideRepoDetailsUseCase(impl: AppRepoDetailsUseCase): RepoDetailsUseCase
}
