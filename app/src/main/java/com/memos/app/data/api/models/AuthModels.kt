package com.memos.app.data.api.models

import com.google.gson.annotations.SerializedName

data class SignInRequest(
    @SerializedName("username")    val username: String,
    @SerializedName("password")    val password: String,
    @SerializedName("neverExpire") val neverExpire: Boolean = false
)

// POST /api/v1/auth/signin response — returns accessToken directly
data class AuthResponse(
    @SerializedName("accessToken") val accessToken: String = ""
)

// GET /api/v1/auth/me response — { "user": { ... } }
data class GetCurrentUserResponse(
    @SerializedName("user") val user: User? = null
)

