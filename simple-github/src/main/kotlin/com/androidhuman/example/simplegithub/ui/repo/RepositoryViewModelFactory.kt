package com.androidhuman.example.simplegithub.ui.repo

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.androidhuman.example.simplegithub.api.GithubApi

class RepositoryViewModelFactory(val api: GithubApi) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        @Suppress("unchecked_cast")
        return RepositoryViewModel(api) as T
    }
}