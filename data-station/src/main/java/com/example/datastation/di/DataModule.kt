package com.example.datastation.di

import android.content.Context
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.example.datastation.BuildConfig
import com.example.datastation.data.api.Api
import com.example.datastation.data.repository.RepositoryImpl
import com.example.domain.domain.repository.Repository
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.Reusable
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface DataModule {

    @Binds
    @Reusable
    fun bindRepository(useCase: RepositoryImpl): Repository

    companion object {
        private const val TIME_OUT = 30L
        private const val API_KEY = "key"

        private val json = Json {
            ignoreUnknownKeys = true
        }

        @Singleton
        @Provides
        fun provideInterceptor() = Interceptor { chain ->
            val original = chain.request()
            val originalHttpUrl = original.url
            val url = originalHttpUrl.newBuilder().addQueryParameter(API_KEY, BuildConfig.API_KEY).build()
            val reqBuilder = original.newBuilder().url(url)
            chain.proceed(reqBuilder.build())
        }

        @Singleton
        @Provides
        fun provideOkHttpClient(
            interceptor: Interceptor,
            @ApplicationContext context: Context,
        ): OkHttpClient =
            OkHttpClient.Builder().connectTimeout(TIME_OUT, TimeUnit.SECONDS).readTimeout(TIME_OUT, TimeUnit.SECONDS)
                .addNetworkInterceptor(ChuckerInterceptor.Builder(context).build()).addInterceptor(interceptor).build()

        @Singleton
        @Provides
        fun provideAPI(okHttpClient: OkHttpClient): Api {
            val contentType = "application/json".toMediaType()
            return Retrofit.Builder().baseUrl(BuildConfig.BASE_URL).client(okHttpClient).addConverterFactory(
                json.asConverterFactory(contentType)
            ).build().create(Api::class.java)
        }
    }
}
