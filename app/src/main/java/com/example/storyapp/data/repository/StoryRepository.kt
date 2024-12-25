package com.example.storyapp.data.repository

import android.content.Context
import android.content.SharedPreferences
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.storyapp.App
import com.example.storyapp.data.remote.request.RegisterRequest
import com.example.storyapp.data.remote.response.ApiResponse
import com.example.storyapp.data.remote.response.Story
import com.example.storyapp.data.remote.response.StoryDetailResponse
import com.example.storyapp.data.remote.retrofit.ApiConfig
import com.example.storyapp.ui.home.StoryPagingSource
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

open class StoryRepository {
    private val api = ApiConfig.getApiService()
    private val sharedPreferences: SharedPreferences
        get() = App.context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    open fun getStories(): Flow<PagingData<Story>> {
        val token = sharedPreferences.getString("token", null)

        if (token.isNullOrEmpty()) {
            throw IllegalStateException("Token not found")
        }

        return Pager(
            config = PagingConfig(
                pageSize = 10,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { StoryPagingSource(api, token) }
        ).flow
    }

    suspend fun fetchStoriesWithLocation(): Result<List<Story>> {
        val token = sharedPreferences.getString("token", null)

        if (token.isNullOrEmpty()) {
            return Result.failure(Exception("Token not found"))
        }

        return try {
            val response = api.getStoriesWithLocation("Bearer $token")
            if (!response.error) {
                Result.success(response.listStory ?: emptyList())
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun fetchStoryDetail(storyId: String, onResult: (Story?, String?) -> Unit) {
        val token = sharedPreferences.getString("token", null)

        if (token.isNullOrEmpty()) {
            onResult(null, "Token not found")
            return
        }

        api.getStoryDetail("Bearer $token", storyId).enqueue(object : Callback<StoryDetailResponse> {
            override fun onResponse(call: Call<StoryDetailResponse>, response: Response<StoryDetailResponse>) {
                if (response.isSuccessful) {
                    val story = response.body()?.story
                    onResult(story, null)
                } else {
                    onResult(null, "Error: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<StoryDetailResponse>, t: Throwable) {
                onResult(null, t.message)
            }
        })
    }

    fun uploadStory(
        photo: MultipartBody.Part,
        description: RequestBody,
        lat: RequestBody? = null,
        lon: RequestBody? = null,
        onResult: (ApiResponse?, String?) -> Unit
    ) {
        val token = sharedPreferences.getString("token", null)

        val call = api.addStory("Bearer $token", photo, description, lat, lon)
        call.enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    onResult(response.body(), null)
                } else {
                    onResult(null, "Error: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                onResult(null, t.message)
            }
        })
    }

    fun registerUser(
        name: String,
        email: String,
        password: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        val requestBody = RegisterRequest(name, email, password)
        api.registerUser(requestBody).enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                if (response.isSuccessful) {
                    onResult(true, response.body()?.message)
                } else {
                    val errorMessage = response.errorBody()?.string() ?: response.message()
                    onResult(false, errorMessage)
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                onResult(false, t.message)
            }
        })
    }
}