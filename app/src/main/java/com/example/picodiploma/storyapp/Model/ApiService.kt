package com.example.picodiploma.storyapp.Model



import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST


interface ApiService {
    @POST("/v1/register")
    suspend fun registerUser(
        @Body userRegistration: UserRegistration
    ): RegisterResponse
    @POST("/v1/login")
    suspend fun loginUser(
        @Body request: LoginRequest
    ): Response<LoginResponse>


}


