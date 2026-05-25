package com.memos.app.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.memos.app.data.api.models.Memo
import com.memos.app.data.repository.AuthRepository
import com.memos.app.data.repository.MemoRepository
import com.memos.app.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val memos        : List<Memo> = emptyList(),
    val isFirstLoad  : Boolean    = true,
    val isRefreshing : Boolean    = false,
    val error        : String?    = null,
    val searchQuery  : String     = ""
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val memoRepo : MemoRepository,
    private val authRepo : AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(HomeUiState())
    val state: StateFlow<HomeUiState> = _state.asStateFlow()

    val currentUser = authRepo.getSavedUser()

    // All raw memos coming from Room
    private val _allMemos = MutableStateFlow<List<Memo>>(emptyList())

    init {
        // 1️⃣  Collect local cache → instant display
        viewModelScope.launch {
            memoRepo.localMemos.collect { cached ->
                _allMemos.value = cached
                _state.update { s ->
                    s.copy(
                        memos       = applyFilter(cached, s.searchQuery),
                        isFirstLoad = cached.isEmpty() && s.isFirstLoad
                    )
                }
            }
        }
        // 2️⃣  Refresh from network in background
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _state.update { it.copy(isRefreshing = true) }
            when (val r = memoRepo.refresh()) {
                is Result.Error -> _state.update {
                    it.copy(isRefreshing = false, error = r.message)
                }
                else -> _state.update {
                    it.copy(isRefreshing = false, isFirstLoad = false, error = null)
                }
            }
        }
    }

    fun search(q: String) {
        _state.update { s ->
            s.copy(
                searchQuery = q,
                memos       = applyFilter(_allMemos.value, q)
            )
        }
    }

    fun deleteMemo(memo: Memo) {
        viewModelScope.launch { memoRepo.deleteMemo(memo) }
    }

    fun logout() {
        viewModelScope.launch { authRepo.signOut() }
    }

    private fun applyFilter(memos: List<Memo>, q: String) =
        if (q.isBlank()) memos
        else memos.filter {
            it.content.contains(q, ignoreCase = true) ||
            it.tags.any { t -> t.contains(q, ignoreCase = true) }
        }
}
