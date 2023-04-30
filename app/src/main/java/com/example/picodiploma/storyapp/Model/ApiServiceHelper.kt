package com.example.picodiploma.storyapp.Model

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiServiceHelper(private val token: String?) {

    private val apiService: ApiService

    init {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(ApiInterceptor(token))
            .build()
        val retrofit = Retrofit.Builder()
            .baseUrl("https://story-api.dicoding.dev/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
        apiService = retrofit.create(ApiService::class.java)
    }

    suspend fun login(email: String, password: String): LoginResponse {
        val loginRequest = LoginRequest(email, password)
        val response = apiService.loginUser(loginRequest)
        return response.body() ?: LoginResponse(error = true, message = "Unknown error")
    }

    suspend fun registerUser(userRegistration: UserRegistration): RegisterResponse {
        return apiService.registerUser(userRegistration)
    }

    suspend fun getStoryList(page: Int = 0, size: Int = 10, location: Int = 0): List<Story> {
        val authorization = "Bearer $token"
        val response = apiService.getStoryList(authorization, page, size, location)
        if (response.isSuccessful) {
            return response.body()?.listStory ?: emptyList()
        } else {
            val errorBody = response.errorBody()?.string()
            throw Exception("Failed to get story list: $errorBody")
        }
    }

}


