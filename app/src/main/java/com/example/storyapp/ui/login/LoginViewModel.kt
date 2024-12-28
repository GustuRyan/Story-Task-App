package com.example.storyapp.ui.login

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.storyapp.data.remote.request.LoginRequest
import com.example.storyapp.data.remote.response.ApiResponse
import com.example.storyapp.data.remote.response.LoginResponse
import com.example.storyapp.data.remote.retrofit.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val apiService = ApiConfig.getApiService()

    val loginResponse = MutableLiveData<ApiResponse>()
    val errorMessage = MutableLiveData<String>()

    fun loginUser(email: String, password: String) {
        val loginRequest = LoginRequest(email, password)
        apiService.loginUser(loginRequest).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    val loginResult = response.body()?.loginResult
                    if (loginResult != null) {
                        val sharedPreferences = getApplication<Application>().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                        val editor = sharedPreferences.edit()
                        editor.putBoolean("is_logged_in", true)
                        editor.putString("token", loginResult.token)
                        editor.apply()

                        loginResponse.value = ApiResponse(false, "Login Berhasil")
                    }
                } else {
                    errorMessage.value = "Login gagal: ${response.message()}"
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                errorMessage.value = t.message
            }
        })
    }
}