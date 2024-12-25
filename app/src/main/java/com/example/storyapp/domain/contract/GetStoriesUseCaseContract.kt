package com.example.storyapp.domain.contract

import androidx.paging.PagingData
import com.example.storyapp.domain.entity.StoryEntity
import kotlinx.coroutines.flow.Flow

interface GetStoriesUseCaseContract {
    operator fun invoke(): Flow<PagingData<StoryEntity>>
}