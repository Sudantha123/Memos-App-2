package com.memos.app.ui.screens.memo

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.memos.app.data.api.models.Memo
import com.memos.app.data.repository.MemoRepository
import com.memos.app.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MemoViewModel @Inject constructor(
    private val repo: MemoRepository
) : ViewModel() {

    var content     by mutableStateOf("")
    var visibility  by mutableStateOf("PRIVATE")
    var isLoading   by mutableStateOf(false)
    var isSaved     by mutableStateOf(false)
    var error       by mutableStateOf<String?>(null)
    var currentMemo by mutableStateOf<Memo?>(null)

    fun loadMemo(name: String) {
        viewModelScope.launch {
            repo.localMemos.collect { list ->
                val m = list.find { it.name == name }
                if (m != null) {
                    currentMemo = m
                    content    = m.content
                    visibility = m.visibility
                }
            }
        }
    }

    fun save(memoName: String? = null) {
        if (content.isBlank()) { error = "Content cannot be empty"; return }
        isLoading = true; error = null

        viewModelScope.launch {
            val result = if (memoName != null)
                repo.updateMemo(memoName, content, visibility)
            else
                repo.createMemo(content, visibility)

            when (result) {
                is Result.Success -> isSaved = true
                is Result.Error   -> error   = result.message
                else              -> {}
            }
            isLoading = false
        }
    }
}
