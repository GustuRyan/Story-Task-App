package com.example.storyapp.ui.detail_story

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.storyapp.data.remote.response.Story
import com.example.storyapp.data.repository.StoryRepository

class DetailStoryViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = StoryRepository()

    private val _storyDetail = MutableLiveData<Story?>()
    val storyDetail: LiveData<Story?> get() = _storyDetail

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    fun fetchStoryDetail(storyId: String) {
        repository.fetchStoryDetail(storyId) { story, error ->
            if (error != null) {
                _errorMessage.postValue(error)
            } else {
                _storyDetail.postValue(story)
            }
        }
    }
}
