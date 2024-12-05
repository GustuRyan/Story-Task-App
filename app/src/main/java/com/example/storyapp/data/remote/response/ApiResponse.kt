package com.example.storyapp.data.remote.response
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ApiResponse(
    val error: Boolean,
    val message: String,
    val listStory: List<Story>? = null
) : Parcelable

@Parcelize
data class Story(
    val id: String,
    val name: String,
    val description: String,
    val photoUrl: String,
    val createdAt: String,
    val lat: Double,
    val lon: Double
) : Parcelable