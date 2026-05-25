package com.memos.app.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "memos")
data class MemoEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val content: String,
    val visibility: String,
    val pinned: Boolean,
    val createTime: String,
    val updateTime: String,
    val tagsJson: String,
    val creatorId: Int,
    val rowStatus: String,
    val snippet: String,
    val cachedAt: Long = System.currentTimeMillis()
)
