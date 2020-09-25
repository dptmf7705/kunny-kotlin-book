package com.androidhuman.example.simplegithub.data

import android.content.Context
import android.preference.PreferenceManager

class AuthTokenProvider(private val context: Context) {

    fun updateToken(token: String) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putString(KEY_AUTH_TOKEN, token)
                .apply()
    }

    val token: String?
        get() = PreferenceManager.getDefaultSharedPreferences(context)
                .getString(KEY_AUTH_TOKEN, null)

    companion object {
        // 상수로 선언하면 자바의 정적 필드처럼 취급
        private const val KEY_AUTH_TOKEN = "auth_token"
    }
}