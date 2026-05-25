package com.memos.app.ui.screens.settings

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import com.memos.app.utils.PreferenceManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ServerSetupViewModel @Inject constructor(
    val preferenceManager: PreferenceManager
) : ViewModel() {

    var serverUrl    by mutableStateOf(preferenceManager.getServerUrl() ?: "")
    var urlError     by mutableStateOf<String?>(null)
    var setupComplete by mutableStateOf(false)

    fun save() {
        val url = serverUrl.trim()
        when {
            url.isEmpty() ->
                urlError = "URL cannot be empty"
            !url.startsWith("http://") && !url.startsWith("https://") ->
                urlError = "Must start with http:// or https://"
            else -> {
                urlError = null
                preferenceManager.saveServerUrl(url)
                setupComplete = true
            }
        }
    }
}
