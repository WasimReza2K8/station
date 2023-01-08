package com.example.domain.di

import com.example.domain.domain.usecase.GetStationsUseCase
import com.example.domain.domain.usecase.GetStationsUseCaseImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
interface DomainModule {
    @Binds
    @ViewModelScoped
    fun provideGetStationsUseCase(useCase: GetStationsUseCaseImpl): GetStationsUseCase
}
