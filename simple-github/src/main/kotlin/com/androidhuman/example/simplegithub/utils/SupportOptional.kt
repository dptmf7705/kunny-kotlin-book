package com.androidhuman.example.simplegithub.utils

sealed class SupportOptional<out T : Any>(private val _value: T?) {
    val isEmpty: Boolean
        get() = null == _value

    val isNotEmpty: Boolean
        get() = null != _value

    val value: T
        get() = checkNotNull(_value)
}

// 빈 데이터 표시
class Empty<out T : Any> : SupportOptional<T>(null)

// 널이 아닌 값 표시
class Some<out T : Any>(value: T) : SupportOptional<T>(value)

inline fun <reified T : Any> optionalOf(value: T?) =
    if (null != value) Some(value) else Empty<T>()

inline fun <reified T : Any> emptyOptional() = Empty<T>()