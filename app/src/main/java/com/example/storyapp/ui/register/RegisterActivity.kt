package com.example.storyapp.ui.register

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import com.example.storyapp.R
import com.example.storyapp.data.repository.StoryRepository
import com.example.storyapp.databinding.ActivityRegisterBinding
import com.example.storyapp.ui.login.LoginActivity
import android.widget.Toast

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var viewModel: RegisterViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inisialisasi ViewModel
        val repository = StoryRepository()
        val factory = RegisterViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[RegisterViewModel::class.java]

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.progressBar.visibility = View.GONE

        binding.edRegisterName.addTextChangedListener { validateInputs() }
        binding.edRegisterEmail.addTextChangedListener { validateInputs() }
        binding.edRegisterPassword.addTextChangedListener { validateInputs() }
        binding.edConfirmPassword.addTextChangedListener { validateInputs() }

        binding.tvLogin.setOnClickListener {
            navigateToLogin()
        }

        binding.myButton.setOnClickListener {
            registerUser()
            binding.progressBar.visibility = View.VISIBLE
        }

        observeViewModel()
        playAnimation()
    }

    private fun validateInputs() {
        val nameText = binding.edRegisterName.text?.toString()?.trim()
        val emailText = binding.edRegisterEmail.text?.toString()?.trim()
        val passwordText = binding.edRegisterPassword.text?.toString()?.trim()
        val confirmPasswordText = binding.edConfirmPassword.text?.toString()?.trim()

        val isInputValid = !nameText.isNullOrEmpty() &&
                !emailText.isNullOrEmpty() &&
                !passwordText.isNullOrEmpty() &&
                !confirmPasswordText.isNullOrEmpty()

        val isPasswordMatch = passwordText == confirmPasswordText

        if (!isPasswordMatch) {
            binding.edConfirmPassword.error = getString(R.string.password_not_match)
        } else {
            binding.edConfirmPassword.error = null
        }

        binding.myButton.isEnabled = isInputValid && isPasswordMatch
    }

    private fun registerUser() {
        val name = binding.edRegisterName.text.toString().trim()
        val email = binding.edRegisterEmail.text.toString().trim()
        val password = binding.edRegisterPassword.text.toString().trim()

        binding.myButton.isEnabled = false

        viewModel.registerUser(name, email, password)
    }

    private fun observeViewModel() {
        viewModel.registerResult.observe(this) { result ->
            binding.progressBar.visibility = View.GONE
            binding.myButton.isEnabled = true

            result.onSuccess {
                Toast.makeText(this, getString(R.string.register_success), Toast.LENGTH_SHORT).show()
                navigateToLogin()
            }

            result.onFailure { exception ->
                Toast.makeText(this, exception.message ?: getString(R.string.register_failed), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.ivRegister, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 5000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        AnimatorSet().apply {
            start()
        }
    }
}