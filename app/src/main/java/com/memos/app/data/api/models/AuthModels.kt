package com.memos.app.data.api.models

import com.google.gson.annotations.SerializedName

data class SignInRequest(
    @SerializedName("username") val username: String,
    @SerializedName("password") val password: String
)

data class AuthResponse(
    @SerializedName("id")          val id: Int    = 0,
    @SerializedName("username")    val username: String = "",
    @SerializedName("displayName") val displayName: String = ""
)
