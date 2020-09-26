package com.androidhuman.example.simplegithub.extensions

import io.reactivex.Completable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

operator fun CompositeDisposable.plusAssign(disposable: Disposable) {
    this.add(disposable)
}

operator fun AutoClearedDisposable.plusAssign(disposable: Disposable) {
    this.add(disposable)
}

fun runOnIOScheduler(func: () -> Unit): Disposable =
    Completable.fromCallable(func)
        .subscribeOn(Schedulers.io())
        .subscribe()