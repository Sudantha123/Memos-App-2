package com.memos.app.data.repository

import com.memos.app.data.api.MemoApiService
import com.memos.app.data.api.models.SignInRequest
import com.memos.app.data.api.models.User
import com.memos.app.utils.PreferenceManager
import com.memos.app.utils.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val api: MemoApiService,
    private val prefs: PreferenceManager
) {

    // Username + Password login
    suspend fun signIn(username: String, password: String): Result<User> =
        withContext(Dispatchers.IO) {
            try {
                val signInResp = api.signIn(SignInRequest(username, password))
                if (!signInResp.isSuccessful) {
                    return@withContext Result.Error("Login failed (${signInResp.code()})")
                }
                val token = signInResp.body()?.accessToken
                if (!token.isNullOrBlank()) {
                    prefs.saveAccessToken(token)
                }
                fetchAndSaveCurrentUser()
            } catch (e: Exception) {
                Result.Error(e.message ?: "Network error")
            }
        }

    // Access Token direct login
    // Flow: save token → GET /api/v1/auth/me → verify → save user
    suspend fun signInWithToken(token: String): Result<User> =
        withContext(Dispatchers.IO) {
            try {
                prefs.saveAccessToken(token)
                val result = fetchAndSaveCurrentUser()
                if (result is Result.Error) {
                    prefs.saveAccessToken("")
                }
                result
            } catch (e: Exception) {
                prefs.saveAccessToken("")
                Result.Error(e.message ?: "Network error")
            }
        }

    // GET /api/v1/auth/me — response: { "user": { ... } }
    private suspend fun fetchAndSaveCurrentUser(): Result<User> {
        val resp = api.getCurrentUser()
        return if (resp.isSuccessful) {
            val user = resp.body()?.user
                ?: return Result.Error("Invalid token (404)")
            prefs.saveUser(user)
            Result.Success(user)
        } else {
            Result.Error("Invalid token (${resp.code()})")
        }
    }

    suspend fun signOut(): Result<Unit> = withContext(Dispatchers.IO) {
        try { api.signOut() } catch (_: Exception) {}
        prefs.clearAll()
        Result.Success(Unit)
    }

    suspend fun refreshCurrentUser(): Result<User> =
        withContext(Dispatchers.IO) { fetchAndSaveCurrentUser() }

    fun isLoggedIn()   = prefs.isLoggedIn()
    fun getServerUrl() = prefs.getServerUrl()
    fun getSavedUser() = prefs.getSavedUser()
}

