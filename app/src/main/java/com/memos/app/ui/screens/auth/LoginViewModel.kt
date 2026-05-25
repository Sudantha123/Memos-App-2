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

    var username     by mutableStateOf("")
    var password     by mutableStateOf("")
    var isLoading    by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)
    var loginSuccess by mutableStateOf(false)

    fun serverUrl() = auth.getServerUrl()

    fun login() {
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
}
