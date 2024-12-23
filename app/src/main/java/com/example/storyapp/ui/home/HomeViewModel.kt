package com.example.storyapp.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.storyapp.data.remote.response.Story
import com.example.storyapp.data.repository.StoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest

class HomeViewModel(private val storyRepository: StoryRepository) : ViewModel() {

    private val _refreshTrigger = MutableStateFlow(Unit)
    val stories: Flow<PagingData<Story>> = _refreshTrigger
        .flatMapLatest {
            storyRepository.getStories()
        }
        .catch { e ->
            handleError(e.message)
        }
        .cachedIn(viewModelScope)

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    fun handleError(error: String?) {
        _errorMessage.postValue(error)
    }

    fun refresh() {
        _refreshTrigger.value = Unit
    }
}

class ViewModelFactory(private val storyRepository: StoryRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(storyRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}