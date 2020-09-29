package com.androidhuman.example.simplegithub.di

import com.androidhuman.example.simplegithub.api.AuthApi
import com.androidhuman.example.simplegithub.api.GithubApi
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.CallAdapter
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

// Module: 필요한 객체 제공하는 역할
// Provides: 모듈에서 제공하는 객체를 정의한 함수

@Module
class ApiModule {

    /* 객체 생성 시 필요한 객체들은 함수의 인자로 전달 */

    // AuthApi 객체 제공
    @Provides
    @Singleton
    fun provideAuthApi(
        @Named("unauthorized") okHttpClient: OkHttpClient,
        callAdapter: CallAdapter.Factory,
        converter: Converter.Factory
    ): AuthApi = Retrofit.Builder()
        .baseUrl("https://github.com/")
        .client(okHttpClient)
        .addCallAdapterFactory(callAdapter)
        .addConverterFactory(converter)
        .build()
        .create(AuthApi::class.java)

    // GithubApi 객체 제공
    @Provides
    @Singleton
    fun provideGithubApi(
        @Named("authorized") okHttpClient: OkHttpClient,
        callAdapter: CallAdapter.Factory,
        converter: Converter.Factory
    ): GithubApi = Retrofit.Builder()
        .baseUrl("https://api.github.com/")
        .client(okHttpClient)
        .addCallAdapterFactory(callAdapter)
        .addConverterFactory(converter)
        .build()
        .create(GithubApi::class.java)


    /* AuthApi, GithubApi 객체 생성 시 필요한 객체들 */

    // RxJava2 CallAdapter.Factory 객체 제공
    @Provides
    @Singleton
    fun provideCallAdapterFactory(): CallAdapter.Factory =
        RxJava2CallAdapterFactory.createAsync()

    // Gson Converter.Factory 객체 제공
    @Provides
    @Singleton
    fun provideConverterFactory(): Converter.Factory =
        GsonConverterFactory.create()
}