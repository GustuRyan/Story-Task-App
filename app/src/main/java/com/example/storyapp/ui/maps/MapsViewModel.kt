package com.example.storyapp.ui.maps

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.storyapp.data.remote.response.Story
import com.example.storyapp.data.repository.StoryRepository
import kotlinx.coroutines.launch

class MapsViewModel(private val repository: StoryRepository) : ViewModel() {

    private val _storiesWithLocation = MutableLiveData<List<Story>?>()
    val storiesWithLocation: LiveData<List<Story>?> = _storiesWithLocation

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    fun fetchStoriesWithLocation() {
        viewModelScope.launch {
            val result = repository.fetchStoriesWithLocation()
            result.onSuccess { stories ->
                _storiesWithLocation.postValue(stories)
            }.onFailure { error ->
                _errorMessage.postValue(error.message)
            }
        }
    }
}