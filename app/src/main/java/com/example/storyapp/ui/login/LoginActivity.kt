package com.example.storyapp.ui.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import com.example.storyapp.ui.MainActivity
import com.example.storyapp.databinding.ActivityLoginBinding
import com.example.storyapp.ui.register.RegisterActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var loginViewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loginViewModel = ViewModelProvider(this).get(LoginViewModel::class.java)

        loginViewModel.loginResponse.observe(this) { response ->
            if (!response.error) {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(this, response.message, Toast.LENGTH_SHORT).show()
                navigateToHome()
            }
        }

        loginViewModel.errorMessage.observe(this) { error ->
            binding.progressBar.visibility = View.GONE
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
        }

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.progressBar.visibility = View.GONE

        binding.edLoginEmail.addTextChangedListener { validateInputs() }
        binding.edLoginPassword.addTextChangedListener { validateInputs() }
        binding.tvRegister.setOnClickListener {
            navigateToRegister()
        }

        binding.myButton.setOnClickListener {
            binding.progressBar.visibility = View.VISIBLE
            val email = binding.edLoginEmail.text.toString().trim()
            val password = binding.edLoginPassword.text.toString().trim()
            loginViewModel.loginUser(email, password)
        }

        playAnimation()
    }

    private fun validateInputs() {
        val emailText = binding.edLoginEmail.text?.toString()?.trim()
        val passwordText = binding.edLoginPassword.text?.toString()?.trim()
        binding.myButton.isEnabled = !emailText.isNullOrEmpty() && !passwordText.isNullOrEmpty()
    }

    private fun navigateToRegister() {
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun navigateToHome() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.ivLogin, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 5000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        AnimatorSet().apply {
            start()
        }
    }
}