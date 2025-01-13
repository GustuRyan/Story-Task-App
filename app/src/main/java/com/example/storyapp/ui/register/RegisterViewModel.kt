package com.example.storyapp.ui.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.storyapp.data.repository.StoryRepository

class RegisterViewModel(private val repository: StoryRepository) : ViewModel() {

    private val _registerResult = MutableLiveData<Result<Boolean>>()
    val registerResult: LiveData<Result<Boolean>> = _registerResult

    fun registerUser(name: String, email: String, password: String) {
        repository.registerUser(name, email, password) { success, message ->
            if (success) {
                _registerResult.postValue(Result.success(true))
            } else {
                _registerResult.postValue(Result.failure(Exception(message)))
            }
        }
    }
}
