package com.example.domain.di

import com.example.domain.domain.usecase.GetStationsUseCase
import com.example.domain.domain.usecase.GetStationsUseCaseImpl
import dagger.Binds
import dagger.Module
import dagger.Reusable
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
interface DomainModule {
    @Reusable
    @Binds
    fun provideGetStationsUseCase(useCase: GetStationsUseCaseImpl): GetStationsUseCase
}
