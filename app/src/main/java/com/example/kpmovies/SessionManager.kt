package com.example.kpmovies

import android.content.Context
import androidx.core.content.edit

object SessionManager {
    private const val PREFS = "user_session"
    private const val KEY_LOGIN = "login"

    fun saveLogin(ctx: Context, login: String) =
        ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit { putString(KEY_LOGIN, login) }

    fun getLogin(ctx: Context): String? =
        ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getString(KEY_LOGIN, null)

    fun clear(ctx: Context) =
        ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit { remove(KEY_LOGIN) }
}
