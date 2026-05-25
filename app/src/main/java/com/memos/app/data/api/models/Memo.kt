package com.memos.app.data.api.models

import com.google.gson.annotations.SerializedName

data class Memo(
    @SerializedName("id")          val id: Int = 0,
    @SerializedName("name")        val name: String = "",
    @SerializedName("uid")         val uid: String = "",
    @SerializedName("rowStatus")   val rowStatus: String = "NORMAL",
    @SerializedName("creator")     val creator: String = "",
    @SerializedName("creatorId")   val creatorId: Int = 0,
    @SerializedName("createTime")  val createTime: String = "",
    @SerializedName("updateTime")  val updateTime: String = "",
    @SerializedName("displayTime") val displayTime: String = "",
    @SerializedName("content")     val content: String = "",
    @SerializedName("visibility")  val visibility: String = "PRIVATE",
    @SerializedName("tags")        val tags: List<String> = emptyList(),
    @SerializedName("pinned")      val pinned: Boolean = false,
    @SerializedName("resources")   val resources: List<MemoResource>? = null,
    @SerializedName("snippet")     val snippet: String = ""
)

data class MemoResource(
    @SerializedName("name")         val name: String = "",
    @SerializedName("filename")     val filename: String = "",
    @SerializedName("externalLink") val externalLink: String = "",
    @SerializedName("type")         val type: String = "",
    @SerializedName("size")         val size: Long = 0
)

data class ListMemosResponse(
    @SerializedName("memos")         val memos: List<Memo> = emptyList(),
    @SerializedName("nextPageToken") val nextPageToken: String = ""
)

data class CreateMemoRequest(
    @SerializedName("content")    val content: String,
    @SerializedName("visibility") val visibility: String = "PRIVATE",
    @SerializedName("tags")       val tags: List<String> = emptyList()
)

data class User(
    @SerializedName("name")        val name: String = "",
    @SerializedName("id")          val id: Int = 0,
    @SerializedName("username")    val username: String = "",
    @SerializedName("email")       val email: String = "",
    @SerializedName("displayName") val displayName: String = "",
    @SerializedName("avatarUrl")   val avatarUrl: String? = null,
    @SerializedName("role")        val role: String = "",
    @SerializedName("description") val description: String = ""
)
