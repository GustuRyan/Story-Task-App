package com.example.storyapp.ui

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import com.example.storyapp.R
import com.example.storyapp.databinding.FragmentStartedBinding


class StartedFragment : Fragment() {
    private var _binding: FragmentStartedBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentStartedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        playAnimation()
        setupAction()

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                requireActivity().finish()
            }
        })
    }

    private fun playAnimation() {
        binding.buttonLogin.alpha = 0f
        binding.buttonRegister.alpha = 0f
        binding.tvStartedTitle.alpha = 0f
        binding.tvStartedDescription.alpha = 0f

        ObjectAnimator.ofFloat(binding.ivStarted, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 5000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val login = ObjectAnimator.ofFloat(binding.buttonLogin, View.ALPHA, 1f).setDuration(700)
        val signup = ObjectAnimator.ofFloat(binding.buttonRegister, View.ALPHA, 1f).setDuration(700)
        val title = ObjectAnimator.ofFloat(binding.tvStartedTitle, View.ALPHA, 1f).setDuration(500)
        val desc = ObjectAnimator.ofFloat(binding.tvStartedDescription, View.ALPHA, 1f).setDuration(600)

        val together = AnimatorSet().apply {
            playTogether(login, signup)
        }

        AnimatorSet().apply {
            playSequentially(title, desc, together)
            start()
        }
    }

    private fun setupAction() {
        val navController = findNavController()

        binding.buttonLogin.setOnClickListener {
            navController.navigate(R.id.action_startedFragment_to_loginActivity)
        }

        binding.buttonRegister.setOnClickListener {
            navController.navigate(R.id.action_startedFragment_to_registerActivity)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}