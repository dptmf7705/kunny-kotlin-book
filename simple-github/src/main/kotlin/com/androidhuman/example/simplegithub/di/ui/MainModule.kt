package com.androidhuman.example.simplegithub.di.ui

import android.arch.lifecycle.ViewModelProviders
import android.support.v4.app.FragmentActivity
import com.androidhuman.example.simplegithub.data.SearchHistoryDao
import com.androidhuman.example.simplegithub.ui.main.MainActivity
import com.androidhuman.example.simplegithub.ui.main.MainViewModel
import com.androidhuman.example.simplegithub.ui.main.MainViewModelFactory
import com.androidhuman.example.simplegithub.ui.search.SearchAdapter
import dagger.Module
import dagger.Provides

@Module
class MainModule {

    @Provides
    fun provideAdapter(activity: MainActivity): SearchAdapter =
        SearchAdapter().apply { setItemClickListener(activity) }

    @Provides
    fun provideViewModelFactory(
        searchHistoryDao: SearchHistoryDao
    ): MainViewModelFactory =
        MainViewModelFactory(searchHistoryDao)

    @Provides
    fun provideViewModel(
        activity: MainActivity,
        viewModelFactory: MainViewModelFactory
    ): MainViewModel =
        ViewModelProviders.of(
            activity, viewModelFactory
        )[MainViewModel::class.java]
}