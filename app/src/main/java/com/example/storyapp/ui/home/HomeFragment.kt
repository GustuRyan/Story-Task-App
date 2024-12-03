package com.example.storyapp.ui.home

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.storyapp.R
import com.example.storyapp.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var homeAdapter: HomeAdapter

    private lateinit var navController: NavController
    private var backPressedTime: Long = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        binding.rvStoryList.visibility = View.GONE

        setupRecyclerView()

        homeViewModel.stories.observe(viewLifecycleOwner) { stories ->
            if (stories != null) {
                homeAdapter.updateData(stories)
                binding.rvStoryList.visibility = View.VISIBLE
                binding.progressBar.visibility = View.GONE
            }
        }

        homeViewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
            binding.progressBar.visibility = View.GONE
        }

        homeViewModel.fetchStories()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController()

        binding.buttonAdd.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_home_to_addStoryFragment)
        }

        binding.buttonLogout.setOnClickListener {
            logout()
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val currentTime = System.currentTimeMillis()
                if (currentTime - backPressedTime < 2000) {
                    requireActivity().finish()
                } else {
                    backPressedTime = currentTime
                    Toast.makeText(requireContext(), "Tekan sekali lagi untuk keluar", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun logout() {
        val sharedPreferences = requireActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            remove("is_logged_in")
            remove("token")
            apply()
        }

        Toast.makeText(requireContext(), "Logout berhasil", Toast.LENGTH_SHORT).show()

        navController.navigate(R.id.startedFragment)
    }

    private fun setupRecyclerView() {
        binding.rvStoryList.layoutManager = LinearLayoutManager(requireContext())

        homeAdapter = HomeAdapter(emptyList()) { storyId ->
            val action = HomeFragmentDirections.actionNavigationHomeToDetailStoryFragment(storyId)
            findNavController().navigate(action)
        }

        binding.rvStoryList.adapter = homeAdapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}