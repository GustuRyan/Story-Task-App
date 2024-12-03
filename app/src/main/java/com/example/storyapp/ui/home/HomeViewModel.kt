package com.example.storyapp.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.storyapp.data.remote.response.Story
import com.example.storyapp.data.repository.StoryRepository

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val storyRepository = StoryRepository()

    private val _stories = MutableLiveData<List<Story>?>()
    val stories: LiveData<List<Story>?> get() = _stories

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    fun fetchStories() {
        storyRepository.fetchStories { stories, error ->
            if (error != null) {
                _errorMessage.postValue(error)
            } else {
                _stories.postValue(stories)
            }
        }
    }
}
