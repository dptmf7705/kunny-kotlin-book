package com.androidhuman.example.simplegithub.di

import android.app.Application
import com.androidhuman.example.simplegithub.SimpleGithubApp
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

// Component: 모듈에서 제공받은 객체들을 필요한 곳에 주입해주는 역할
@Component(
    modules = [
        AppModule::class,
        LocalDataModule::class,
        ApiModule::class,
        NetworkModule::class,
        // 대거의 안드로이드 지원 모듈
        AndroidSupportInjectionModule::class,
        ActivityBinder::class
    ]
)
@Singleton
interface AppComponent : AndroidInjector<SimpleGithubApp> {

    // AppComponent 생성할 때 사용할 빌더 클래스
    @Component.Builder
    interface Builder {

        // BindsInstance: 객체 그래프에 추가할 객체를 선언
        @BindsInstance
        fun application(app: Application): Builder

        fun build(): AppComponent
    }
}