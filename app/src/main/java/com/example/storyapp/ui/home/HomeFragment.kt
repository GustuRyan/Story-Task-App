package com.example.storyapp.ui.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.storyapp.ui.maps.MapsActivity
import com.example.storyapp.R
import com.example.storyapp.data.repository.StoryRepository
import com.example.storyapp.databinding.FragmentHomeBinding
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var homeAdapter: HomeAdapter
    private lateinit var loadStateAdapter: LoadingStateAdapter

    private lateinit var navController: NavController
    private var backPressedTime: Long = 0

    private lateinit var storyRepository: StoryRepository
    private val args: HomeFragmentArgs by navArgs()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        // Initialize storyRepository
        storyRepository = StoryRepository()

        // Instantiate ViewModel using ViewModelFactory
        homeViewModel = ViewModelProvider(
            this,
            ViewModelFactory(storyRepository)
        ).get(HomeViewModel::class.java)

        binding.rvStoryList.visibility = View.GONE
        setupRecyclerView()

        lifecycleScope.launchWhenStarted {
            homeViewModel.stories.collect { pagingData ->
                homeAdapter.submitData(pagingData)
            }
        }

        homeViewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
            binding.progressBar.visibility = View.GONE
        }

        return binding.root
    }

    private fun setupRecyclerView() {
        binding.rvStoryList.layoutManager = LinearLayoutManager(requireContext())

        homeAdapter = HomeAdapter { storyId ->
            val action = HomeFragmentDirections.actionNavigationHomeToDetailStoryFragment(storyId)
            findNavController().navigate(action)
        }

        loadStateAdapter = LoadingStateAdapter { homeAdapter.retry() }

        binding.rvStoryList.adapter = homeAdapter.withLoadStateFooter(loadStateAdapter)

        homeAdapter.addLoadStateListener { loadState ->
            binding.progressBar.isVisible = loadState.refresh is LoadState.Loading
            binding.rvStoryList.isVisible = loadState.refresh is LoadState.NotLoading
        }
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

        binding.buttonMaps.setOnClickListener {
            navigateToMap()
        }

        if (args.shouldRefresh) {
            refreshData()
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

    private fun refreshData() {
        homeViewModel.refresh()
        Toast.makeText(requireContext(), "Memuat ulang data...", Toast.LENGTH_SHORT).show()
    }

    private fun navigateToMap() {
        val intent = Intent(requireContext(), MapsActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
