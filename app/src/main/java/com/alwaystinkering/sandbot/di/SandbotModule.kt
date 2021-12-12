package com.alwaystinkering.sandbot.di

import android.content.Context
import com.alwaystinkering.sandbot.BuildConfig
import com.alwaystinkering.sandbot.api.SandbotService
import com.alwaystinkering.sandbot.repo.HostSelectionInterceptor
import com.alwaystinkering.sandbot.repo.SandbotRepository
import com.alwaystinkering.sandbot.repo.SharedPreferencesManager
import com.alwaystinkering.sandbot.util.StringOrJsonConverterFactory
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
class SandbotModule {

    @Provides
    @Singleton
    fun provideSandbotRepository(sandbotService: SandbotService): SandbotRepository =
        SandbotRepository(sandbotService)

    @Provides
    @Singleton
    fun provideSharedPreferencesManager(context: Context): SharedPreferencesManager =
        SharedPreferencesManager(context)

    @Provides
    @Singleton
    fun provideHttpClient(sharedPreferencesManager: SharedPreferencesManager): OkHttpClient {
        val builder = OkHttpClient.Builder()
        builder.readTimeout(3, TimeUnit.SECONDS)
        builder.connectTimeout(1, TimeUnit.SECONDS)
        builder.interceptors().add(HostSelectionInterceptor(sharedPreferencesManager))
        builder.interceptors().add(HttpLoggingInterceptor().apply {
            level =
                if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
        })
        return builder.build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(httpClient: OkHttpClient): Retrofit.Builder {
        return Retrofit.Builder()
            .baseUrl("http://localhost")
            .addConverterFactory(StringOrJsonConverterFactory())
            .client(httpClient)
    }

    @Provides
    @Singleton
    fun provideSandbotService(retrofit: Retrofit.Builder): SandbotService =
        retrofit.build().create(SandbotService::class.java)
}