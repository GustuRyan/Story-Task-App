package com.example.storyapp.domain.usecase

import androidx.paging.PagingData
import androidx.paging.map
import com.example.storyapp.data.remote.response.Story
import com.example.storyapp.data.repository.StoryRepository
import com.example.storyapp.domain.contract.GetStoriesUseCaseContract
import com.example.storyapp.domain.entity.StoryEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetStoriesUseCase(private val storyRepository: StoryRepository) : GetStoriesUseCaseContract {
    override fun invoke(): Flow<PagingData<StoryEntity>> {
        return storyRepository.getStories().map { pagingData ->
            pagingData.map { story ->
                StoryEntity(
                    id = story.id,
                    name = story.name,
                    description = story.description,
                    photoUrl = story.photoUrl,
                    lng = story.lat,
                    lat = story.lon
                )
            }
        }
    }
}

