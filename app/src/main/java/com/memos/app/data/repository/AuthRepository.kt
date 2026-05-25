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

    suspend fun signIn(username: String, password: String): Result<User> =
        withContext(Dispatchers.IO) {
            try {
                val signInResp = api.signIn(SignInRequest(username, password))
                if (!signInResp.isSuccessful) {
                    return@withContext Result.Error(
                        "Login failed (${signInResp.code()})"
                    )
                }
                val meResp = api.getCurrentUser()
                if (meResp.isSuccessful) {
                    val user = meResp.body()!!
                    prefs.saveUser(user)
                    Result.Success(user)
                } else {
                    Result.Error("Could not fetch profile (${meResp.code()})")
                }
            } catch (e: Exception) {
                Result.Error(e.message ?: "Network error")
            }
        }

    suspend fun signOut(): Result<Unit> = withContext(Dispatchers.IO) {
        try { api.signOut() } catch (_: Exception) {}
        prefs.clearAll()
        Result.Success(Unit)
    }

    suspend fun refreshCurrentUser(): Result<User> = withContext(Dispatchers.IO) {
        try {
            val resp = api.getCurrentUser()
            if (resp.isSuccessful) {
                val user = resp.body()!!
                prefs.saveUser(user)
                Result.Success(user)
            } else {
                Result.Error("${resp.code()}")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Network error")
        }
    }

    fun isLoggedIn() = prefs.isLoggedIn()
    fun getServerUrl() = prefs.getServerUrl()
    fun getSavedUser() = prefs.getSavedUser()
}
