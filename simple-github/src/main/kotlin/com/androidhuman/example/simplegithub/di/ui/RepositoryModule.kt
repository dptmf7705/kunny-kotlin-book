package com.androidhuman.example.simplegithub.di.ui

import android.arch.lifecycle.ViewModelProviders
import com.androidhuman.example.simplegithub.api.GithubApi
import com.androidhuman.example.simplegithub.ui.repo.RepositoryActivity
import com.androidhuman.example.simplegithub.ui.repo.RepositoryViewModel
import com.androidhuman.example.simplegithub.ui.repo.RepositoryViewModelFactory
import dagger.Module
import dagger.Provides

@Module
class RepositoryModule {

    @Provides
    fun provideViewModelFactory(
        githubApi: GithubApi
    ): RepositoryViewModelFactory =
        RepositoryViewModelFactory(githubApi)

    @Provides
    fun provideViewModel(
        activity: RepositoryActivity,
        viewModelFactory: RepositoryViewModelFactory
    ): RepositoryViewModel =
        ViewModelProviders.of(
            activity, viewModelFactory
        )[RepositoryViewModel::class.java]
}
