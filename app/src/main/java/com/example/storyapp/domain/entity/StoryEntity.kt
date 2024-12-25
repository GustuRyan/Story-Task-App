package com.example.storyapp.domain.entity

import com.example.storyapp.data.remote.response.StoryResponse

data class StoryEntity(
    val id: String,
    val name: String,
    val description: String,
    val photoUrl: String,
    val lat: Double,
    val lng: Double,
) {
    fun List<StoryResponse>.toStoryEntities(): List<StoryEntity> {
        return map {
            StoryEntity(
                id = it.id,
                name = it.name,
                description = it.description,
                photoUrl = it.photoUrl,
                lng = it.lat,
                lat = it.lon
            )
        }
    }
}