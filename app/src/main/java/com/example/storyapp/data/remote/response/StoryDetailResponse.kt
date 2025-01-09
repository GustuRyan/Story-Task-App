package com.example.storyapp.data.remote.response
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class StoryDetailResponse(
    val error: Boolean,
    val message: String,
    val story: Story
) : Parcelable
