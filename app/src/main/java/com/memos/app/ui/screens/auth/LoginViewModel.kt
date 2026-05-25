package com.memos.app.ui.screens.auth

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.memos.app.data.repository.AuthRepository
import com.memos.app.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val auth: AuthRepository
) : ViewModel() {

    // Shared
    var isLoading    by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)
    var loginSuccess by mutableStateOf(false)

    // Password tab
    var username by mutableStateOf("")
    var password by mutableStateOf("")

    // Token tab
    var accessToken by mutableStateOf("")

    fun serverUrl() = auth.getServerUrl()

    fun loginWithPassword() {
        if (username.isBlank()) { errorMessage = "Username is required"; return }
        if (password.isBlank()) { errorMessage = "Password is required"; return }

        isLoading    = true
        errorMessage = null

        viewModelScope.launch {
            when (val r = auth.signIn(username, password)) {
                is Result.Success -> loginSuccess = true
                is Result.Error   -> errorMessage = r.message
                else              -> {}
            }
            isLoading = false
        }
    }

    fun loginWithToken() {
        if (accessToken.isBlank()) { errorMessage = "Access token is required"; return }

        isLoading    = true
        errorMessage = null

        viewModelScope.launch {
            when (val r = auth.signInWithToken(accessToken)) {
                is Result.Success -> loginSuccess = true
                is Result.Error   -> errorMessage = r.message
                else              -> {}
            }
            isLoading = false
        }
    }
}
