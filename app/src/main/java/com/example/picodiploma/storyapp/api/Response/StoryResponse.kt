package com.example.picodiploma.storyapp.api.Response

import com.google.gson.annotations.SerializedName
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


data class Story(
    val id: String,
    val name: String,
    val description: String,
    val photoUrl: String,
    val createdAt: String,
    val lat: Double?,
    val lon: Double?
)

data class StoryListResponse(
    val error: Boolean,
    val message: String,
    val listStory: List<Story>
)

data class DetailResponse(
    @SerializedName("error")
    val error: Boolean,
    @SerializedName("message")
    val message: String,
    @SerializedName("story")
    val data: StoryDetailResponse?
)

data class StoryDetailResponse(
    @SerializedName("id")
    val id: String,

    @SerializedName("name")
    val name: String,

    @SerializedName("description")
    val description: String,

    @SerializedName("photoUrl")
    val imageUrl: String,

    @SerializedName("createdAt")
    val createdAt: String,

    @SerializedName("lat")
    val lat: Double? = null,

    @SerializedName("lon")
    val lon: Double? = null
) {
    fun getCreatedDate(): Date? {
        val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
        formatter.timeZone = TimeZone.getTimeZone("UTC")
        return try {
            formatter.parse(createdAt)
        } catch (e: ParseException) {
            null
        }
    }
}

data class AddNewStoryResponse(
    @SerializedName("error")
    val error: Boolean,
    @SerializedName("message")
    val message: String
)





