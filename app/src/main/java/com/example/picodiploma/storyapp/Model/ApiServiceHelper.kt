package com.example.picodiploma.storyapp.Model

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiServiceHelper {
    private var token: String? = null
    private val apiService: ApiService

    init {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
        val retrofit = Retrofit.Builder()
            .baseUrl("https://story-api.dicoding.dev/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
        apiService = retrofit.create(ApiService::class.java)
    }

    /*suspend fun login(email: String, password: String): LoginResponse {
        val loginRequest = LoginRequest(email, password)
        val response = apiService.login(loginRequest)
        if (response.isSuccessful) {
            token = response.body()?.token
        }
        return response.body() ?: LoginResponse(error = true, message = "Unknown error")
    }*/

    suspend fun registerUser(userRegistration: UserRegistration): RegisterResponse {
        return apiService.registerUser(userRegistration)
    }
}

