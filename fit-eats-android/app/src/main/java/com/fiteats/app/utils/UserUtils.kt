package com.fiteats.app.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.fiteats.app.models.UserModel
import com.google.gson.Gson

object UserUtils {

    private const val PREFS_NAME = "user_prefs"
    private const val PREFS_SECURE_NAME = "secure_user_prefs"
    private const val KEY_USER = "key_user"

    private const val KEY_ACCESS_TOKEN = "key_access_token"
    private const val KEY_REFRESH_TOKEN = "key_refresh_token"

    private fun getEncryptedPrefs(context: Context): SharedPreferences {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        return EncryptedSharedPreferences.create(
            context,
            PREFS_SECURE_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    fun saveUser(context: Context, user: UserModel, accessToken: String?, refreshToken: String?) {
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val userJson = Gson().toJson(user)
        editor.putString(KEY_USER, userJson)
        editor.apply()

        val encryptedPrefs = getEncryptedPrefs(context)
        encryptedPrefs.edit().putString(KEY_ACCESS_TOKEN, accessToken).apply()
        encryptedPrefs.edit().putString(KEY_REFRESH_TOKEN, refreshToken).apply()
    }

    fun getUser(context: Context): UserModel? {
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val userJson = sharedPreferences.getString(KEY_USER, null)
        return if (!userJson.isNullOrEmpty())
            GsonUtil.gson.fromJson(
                userJson,
                UserModel::class.java
            ) else null
    }

    fun getAccessToken(context: Context): String? {
        val encryptedPrefs = getEncryptedPrefs(context)
        return encryptedPrefs.getString(KEY_ACCESS_TOKEN, null)
    }

    fun getRefreshToken(context: Context): String? {
        val encryptedPrefs = getEncryptedPrefs(context)
        return encryptedPrefs.getString(KEY_REFRESH_TOKEN, null)
    }

    fun clearUser(context: Context) {
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.remove(KEY_USER)
        editor.apply()

        val encryptedPrefs = getEncryptedPrefs(context)
        encryptedPrefs.edit().remove(KEY_ACCESS_TOKEN).apply()
        encryptedPrefs.edit().remove(KEY_REFRESH_TOKEN).apply()
    }

    fun isLoggedIn(context: Context): Boolean {
        return getUser(context) != null
    }
}
