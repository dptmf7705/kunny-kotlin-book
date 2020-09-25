package com.androidhuman.example.simplegithub.extensions

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.OnLifecycleEvent
import android.support.v7.app.AppCompatActivity
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

class AutoClearedDisposable(
    private val lifecycleOwner: AppCompatActivity,
    private val alwaysClearOnStop: Boolean = true,
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()
) : LifecycleObserver {

    // lifecycleOwner 의 현재 상태가 INITIALIZED 이후의 상태인지 확인
    // 참이 아닌 경우 IllegalStateException 발생
    // 참인 경우 disposable 추가
    fun add(disposable: Disposable) {
        check(lifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.INITIALIZED))
        compositeDisposable += disposable
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun cleanUp() {
        // onStop 일 때 무조건 해제하지 않는 경우
        // 액티비티가 종료되지 않는 시점(다른 액티비티 호출 혹은 화면 꺼짐)에는 디스포저블 해제하지 않도록
        if (!alwaysClearOnStop && !lifecycleOwner.isFinishing) {
            return
        }
        compositeDisposable.clear()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun detachSelf() {
        compositeDisposable.clear()
        // 라이프사이클 구독 해제
        lifecycleOwner.lifecycle -= this
    }
}