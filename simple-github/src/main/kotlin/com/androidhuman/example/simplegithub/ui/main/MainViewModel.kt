package com.androidhuman.example.simplegithub.ui.main

import android.arch.lifecycle.ViewModel
import com.androidhuman.example.simplegithub.api.model.GithubRepo
import com.androidhuman.example.simplegithub.data.SearchHistoryDao
import com.androidhuman.example.simplegithub.extensions.runOnIOScheduler
import com.androidhuman.example.simplegithub.utils.SupportOptional
import com.androidhuman.example.simplegithub.utils.emptyOptional
import com.androidhuman.example.simplegithub.utils.optionalOf
import io.reactivex.Flowable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.BehaviorSubject

class MainViewModel(
    val searchHistoryDao: SearchHistoryDao
) : ViewModel() {

    val message: BehaviorSubject<SupportOptional<String>> = BehaviorSubject.create()

    val searchHistory: Flowable<SupportOptional<List<GithubRepo>>>
        get() = searchHistoryDao.getHistory()
            .map { optionalOf(it) }
            .doOnNext {
                if (it.value.isEmpty()) {
                    message.onNext(optionalOf("No recent repositories."))
                } else {
                    message.onNext(emptyOptional())
                }
            }
            .doOnError {
                message.onNext(optionalOf(it.message ?: "Unexpected error."))
            }
            .onErrorReturn { emptyOptional() }

    fun clearSearchHistory(): Disposable =
        runOnIOScheduler { searchHistoryDao.clearAll() }
}