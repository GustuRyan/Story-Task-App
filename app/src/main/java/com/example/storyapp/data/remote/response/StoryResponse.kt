package com.example.storyapp.data.remote.response

import androidx.paging.PagingData
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.storyapp.domain.entity.StoryEntity
import com.google.gson.annotations.SerializedName

@Entity(tableName = "story")
data class StoryResponse(
    @PrimaryKey @field:SerializedName("id") val id: String,
    val createdAt: String,
    val description: String,
    val lat: Double,
    val lon: Double,
    val name: String,
    val photoUrl: String
)

data class StoryViewState(
    val resultStories: PagingData<StoryEntity> = PagingData.empty()
)