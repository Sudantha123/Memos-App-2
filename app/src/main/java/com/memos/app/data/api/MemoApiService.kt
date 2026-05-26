package com.memos.app.data.api

import com.memos.app.data.api.models.*
import retrofit2.Response
import retrofit2.http.*

interface MemoApiService {

    @POST("api/v1/auth/signin")
    suspend fun signIn(@Body request: SignInRequest): Response<AuthResponse>

    @POST("api/v1/auth/signout")
    suspend fun signOut(): Response<Unit>

    // Correct endpoint: /api/v1/auth/me  (not /api/v1/users/me)
    // Response is wrapped: { "user": { ... } }
    @GET("api/v1/auth/me")
    suspend fun getCurrentUser(): Response<GetCurrentUserResponse>

    @GET("api/v1/memos")
    suspend fun listMemos(
        @Query("pageSize")  pageSize: Int = 30,
        @Query("pageToken") pageToken: String? = null,
        @Query("filter")    filter: String? = null
    ): Response<ListMemosResponse>

    @GET("api/v1/{name}")
    suspend fun getMemo(
        @Path("name", encoded = true) name: String
    ): Response<Memo>

    @POST("api/v1/memos")
    suspend fun createMemo(@Body request: CreateMemoRequest): Response<Memo>

    @PATCH("api/v1/{name}")
    suspend fun updateMemo(
        @Path("name", encoded = true) name: String,
        @Body request: CreateMemoRequest
    ): Response<Memo>

    @DELETE("api/v1/{name}")
    suspend fun deleteMemo(
        @Path("name", encoded = true) name: String
    ): Response<Unit>
}

