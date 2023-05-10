package com.example.picodiploma.storyapp.api




import com.example.picodiploma.storyapp.api.Response.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
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

    @GET("stories/{id}")
    suspend fun getStoryDetail(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): Response<DetailResponse>

    @Multipart
    @POST("stories")
    suspend fun uploadStory(
        @Header("Authorization") token: String,
        @Part("description") description: RequestBody,
        @Part file: MultipartBody.Part,
        @Part("lat") lat: RequestBody?,
        @Part("lon") lon: RequestBody?
    ): AddNewStoryResponse

}


