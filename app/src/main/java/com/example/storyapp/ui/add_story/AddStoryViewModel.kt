package com.example.storyapp.ui.add_story

import androidx.lifecycle.ViewModel
import com.example.storyapp.data.repository.StoryRepository
import okhttp3.MultipartBody
import okhttp3.RequestBody

class AddStoryViewModel(private val storyRepository: StoryRepository) : ViewModel() {

    fun uploadStory(
        photo: MultipartBody.Part,
        description: RequestBody,
        lat: RequestBody? = null,
        lon: RequestBody? = null,
        onResult: (Boolean) -> Unit
    ) {
        storyRepository.uploadStory(photo, description, lat, lon) { apiResponse, error ->
            if (apiResponse != null && apiResponse.error == false) {
                onResult(true)
            } else {
                onResult(false)
            }
        }
    }
}
