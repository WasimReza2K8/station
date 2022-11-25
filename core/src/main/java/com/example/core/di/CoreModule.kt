package com.example.core.di

import com.example.core.dispatcher.BaseDispatcherProvider
import com.example.core.dispatcher.MainDispatcherProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface CoreModule {
    @Singleton
    @Binds
    fun provideDispatcher(dispatcherProvider: MainDispatcherProvider): BaseDispatcherProvider
}
