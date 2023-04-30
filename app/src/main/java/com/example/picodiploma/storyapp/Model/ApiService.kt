package com.example.picodiploma.storyapp.Model



import retrofit2.Response
import retrofit2.http.*


interface ApiService {
    @POST("register")
    suspend fun registerUser(
        @Body userRegistration: UserRegistration
    ): RegisterResponse
    @POST("login")
    suspend fun loginUser(
        @Body request: LoginRequest
    ): Response<LoginResponse>
    @GET("stories")
    suspend fun getStoryList(
        @Header("Authorization") token: String,
        @Query("page") page: Int?,
        @Query("size") size: Int?,
        @Query("location") location: Int?
    ): Response<StoryListResponse>






}


