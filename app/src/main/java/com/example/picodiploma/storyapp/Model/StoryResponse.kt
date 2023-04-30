package com.example.picodiploma.storyapp.Model


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


