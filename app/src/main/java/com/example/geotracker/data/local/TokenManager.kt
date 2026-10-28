package com.example.geotracker.data.local

import android.content.Context
import android.content.SharedPreferences

class TokenManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    fun saveToken(token: String) {
        prefs.edit().putString(KEY_TOKEN, token).apply()
    }

    fun getToken(): String? {
        return prefs.getString(KEY_TOKEN, null)
    }

    fun clearToken() {
        prefs.edit().remove(KEY_TOKEN).apply()
    }

    fun hasToken(): Boolean {
        return !getToken().isNull_orEmpty()
    }

    private fun String?.isNull_orEmpty(): Boolean {
        return this.isNullOrEmpty()
    }

    companion object {
        private const val PREF_NAME = "geotracker_prefs"
        private const val KEY_TOKEN = "jwt_token"

        @Volatile
        private var instance: TokenManager? = null

        fun getInstance(context: Context): TokenManager {
            return instance ?: synchronized(this) {
                instance ?: TokenManager(context.applicationContext).also { instance = it }
            }
        }
    }
}
