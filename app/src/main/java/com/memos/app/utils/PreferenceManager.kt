package com.memos.app.utils

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.memos.app.data.api.models.User
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferenceManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val gson: Gson
) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("memos_prefs", Context.MODE_PRIVATE)

    companion object {
        const val KEY_SERVER_URL = "server_url"
        const val KEY_IS_LOGGED_IN = "is_logged_in"
        const val KEY_USER = "user_data"
    }

    fun saveServerUrl(url: String) =
        prefs.edit().putString(KEY_SERVER_URL, url).apply()

    fun getServerUrl(): String? =
        prefs.getString(KEY_SERVER_URL, null)

    fun setLoggedIn(value: Boolean) =
        prefs.edit().putBoolean(KEY_IS_LOGGED_IN, value).apply()

    fun isLoggedIn(): Boolean =
        prefs.getBoolean(KEY_IS_LOGGED_IN, false)

    fun saveUser(user: User) {
        prefs.edit()
            .putString(KEY_USER, gson.toJson(user))
            .putBoolean(KEY_IS_LOGGED_IN, true)
            .apply()
    }

    fun getSavedUser(): User? {
        val json = prefs.getString(KEY_USER, null) ?: return null
        return try {
            gson.fromJson(json, User::class.java)
        } catch (e: Exception) {
            null
        }
    }

    fun clearAll() = prefs.edit().clear().apply()
}
