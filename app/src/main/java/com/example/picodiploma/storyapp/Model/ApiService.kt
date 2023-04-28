package com.example.picodiploma.storyapp.Model


import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import okhttp3.logging.HttpLoggingInterceptor

interface ApiService {

    @POST("/v1/auth/register")
    suspend fun registerUser(
        @Header("Authorization") token: String,
        @Body userRegistration: UserRegistration
    ): RegisterResponse

}

class ApiServiceHelper(private val apiKey: String) {
    private val apiService: ApiService

    init {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor { chain ->
                val original = chain.request()
                val requestBuilder = original.newBuilder()
                    .header("Authorization", "Bearer $apiKey")
                    .method(original.method, original.body)
                val request = requestBuilder.build()
                chain.proceed(request)
            }
            .build()
        val retrofit = Retrofit.Builder()
            .baseUrl("https://story-api.dicoding.dev/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
        apiService = retrofit.create(ApiService::class.java)
    }

    suspend fun registerUser(userRegistration: UserRegistration): RegisterResponse {
        return apiService.registerUser("Bearer $apiKey", userRegistration)
    }
}


