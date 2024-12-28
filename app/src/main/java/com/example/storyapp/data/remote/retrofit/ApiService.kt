package com.example.storyapp.data.remote.retrofit
import com.example.storyapp.data.remote.request.LoginRequest
import com.example.storyapp.data.remote.request.RegisterRequest
import com.example.storyapp.data.remote.response.ApiResponse
import com.example.storyapp.data.remote.response.LoginResponse
import com.example.storyapp.data.remote.response.StoryDetailResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    // Register
    @POST("register")
    fun registerUser(
        @Body requestBody: RegisterRequest
    ): Call<ApiResponse>

    // Login
    @POST("login")
    fun loginUser(
        @Body requestBody: LoginRequest
    ): Call<LoginResponse>

    // Add New Story (with Authentication)
    @Multipart
    @POST("stories")
    fun addStory(
        @Header("Authorization") token: String,
        @Part photo: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("lat") lat: RequestBody? = null,
        @Part("lon") lon: RequestBody? = null
    ): Call<ApiResponse>

    // Add New Story (Guest Account)
    @Multipart
    @POST("stories/guest")
    fun addStoryGuest(
        @Part photo: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("lat") lat: RequestBody? = null,
        @Part("lon") lon: RequestBody? = null
    ): Call<ApiResponse>

    // Get All Stories
    @GET("stories")
    suspend fun getAllStories(
        @Header("Authorization") token: String,
        @Query("page") page: Int = 1,
        @Query("size") size: Int = 20,
        @Query("location") location: Int? = null
    ): ApiResponse

    // Get Story Detail
    @GET("stories/{id}")
    fun getStoryDetail(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): Call<StoryDetailResponse>

    // Get Story with Location
    @GET("stories")
    suspend fun getStoriesWithLocation(
        @Header("Authorization") token: String,
        @Query("location") location: Int = 1
    ): ApiResponse

}