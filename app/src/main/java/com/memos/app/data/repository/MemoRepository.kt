package com.memos.app.data.repository

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.memos.app.data.api.MemoApiService
import com.memos.app.data.api.models.CreateMemoRequest
import com.memos.app.data.api.models.Memo
import com.memos.app.data.local.MemoDao
import com.memos.app.data.local.entities.MemoEntity
import com.memos.app.utils.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MemoRepository @Inject constructor(
    private val api: MemoApiService,
    private val dao: MemoDao,
    private val gson: Gson
) {
    // ── Instant local stream ──────────────────────────────────────────────────
    val localMemos: Flow<List<Memo>> =
        dao.getAllMemos().map { list -> list.map { it.toDomain(gson) } }

    // ── Network refresh ───────────────────────────────────────────────────────
    suspend fun refresh(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val resp = api.listMemos(pageSize = 50)
            if (resp.isSuccessful) {
                val memos = resp.body()?.memos ?: emptyList()
                dao.insertMemos(memos.map { it.toEntity(gson) })
                Result.Success(Unit)
            } else {
                Result.Error("Server error ${resp.code()}")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Network error")
        }
    }

    suspend fun createMemo(content: String, visibility: String): Result<Memo> =
        withContext(Dispatchers.IO) {
            try {
                val tags = parseTags(content)
                val resp = api.createMemo(
                    CreateMemoRequest(content, visibility, tags)
                )
                if (resp.isSuccessful) {
                    val memo = resp.body()!!
                    dao.insertMemo(memo.toEntity(gson))
                    Result.Success(memo)
                } else {
                    Result.Error("Create failed ${resp.code()}")
                }
            } catch (e: Exception) {
                Result.Error(e.message ?: "Network error")
            }
        }

    suspend fun updateMemo(name: String, content: String, visibility: String): Result<Memo> =
        withContext(Dispatchers.IO) {
            try {
                val tags = parseTags(content)
                val resp = api.updateMemo(name, CreateMemoRequest(content, visibility, tags))
                if (resp.isSuccessful) {
                    val memo = resp.body()!!
                    dao.insertMemo(memo.toEntity(gson))
                    Result.Success(memo)
                } else {
                    Result.Error("Update failed ${resp.code()}")
                }
            } catch (e: Exception) {
                Result.Error(e.message ?: "Network error")
            }
        }

    suspend fun deleteMemo(memo: Memo): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val resp = api.deleteMemo(memo.name)
            if (resp.isSuccessful || resp.code() == 404) {
                dao.deleteMemoById(memo.id)
                Result.Success(Unit)
            } else {
                Result.Error("Delete failed ${resp.code()}")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Network error")
        }
    }

    fun searchLocal(query: String): Flow<List<Memo>> =
        dao.searchMemos(query).map { list -> list.map { it.toDomain(gson) } }

    // ── Helpers ───────────────────────────────────────────────────────────────
    private fun parseTags(content: String): List<String> =
        Regex("#([\\w\\u0080-\\uFFFF/]+)")
            .findAll(content).map { it.groupValues[1] }.toList()
}

// ── Mappers ───────────────────────────────────────────────────────────────────
fun Memo.toEntity(gson: Gson) = MemoEntity(
    id         = id,
    name       = name,
    content    = content,
    visibility = visibility,
    pinned     = pinned,
    createTime = createTime,
    updateTime = updateTime,
    tagsJson   = gson.toJson(tags),
    creatorId  = creatorId,
    rowStatus  = rowStatus,
    snippet    = snippet
)

fun MemoEntity.toDomain(gson: Gson): Memo {
    val tags: List<String> = try {
        gson.fromJson(tagsJson, object : TypeToken<List<String>>() {}.type)
    } catch (_: Exception) { emptyList() }

    return Memo(
        id         = id,
        name       = name,
        content    = content,
        visibility = visibility,
        pinned     = pinned,
        createTime = createTime,
        updateTime = updateTime,
        tags       = tags,
        creatorId  = creatorId,
        rowStatus  = rowStatus,
        snippet    = snippet
    )
}
