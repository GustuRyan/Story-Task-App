package com.example.storyapp.ui.detail_story

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.storyapp.databinding.FragmentDetailStoryBinding

class DetailStoryFragment : Fragment() {

    private var storyId: String? = null
    private lateinit var detailStoryViewModel: DetailStoryViewModel

    private var _binding: FragmentDetailStoryBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            storyId = DetailStoryFragmentArgs.fromBundle(it).storyId
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailStoryBinding.inflate(inflater, container, false)
        detailStoryViewModel = ViewModelProvider(this).get(DetailStoryViewModel::class.java)

        binding.ivDetailPhoto.visibility = View.GONE
        binding.tvDetailName.visibility = View.GONE
        binding.tvDetailDescription.visibility = View.GONE
        binding.tvDate.visibility = View.GONE

        observeViewModel()

        storyId?.let { id ->
            detailStoryViewModel.fetchStoryDetail(id)
        }

        return binding.root
    }

    private fun observeViewModel() {
        detailStoryViewModel.storyDetail.observe(viewLifecycleOwner) { story ->
            if (story != null) {
                binding.apply {
                    progressBar.visibility = View.GONE
                    ivDetailPhoto.visibility = View.VISIBLE
                    tvDetailName.visibility = View.VISIBLE
                    tvDetailDescription.visibility = View.VISIBLE
                    tvDate.visibility = View.VISIBLE

                    tvDetailName.text = story.name
                    tvDate.text = if (story.createdAt.length >= 10) {
                        story.createdAt.substring(0, 10)
                    } else {
                        story.createdAt
                    }
                    tvDetailDescription.text = story.description

                    Glide.with(this@DetailStoryFragment)
                        .load(story.photoUrl)
                        .into(ivDetailPhoto)
                }
            }
        }

        detailStoryViewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            if (!error.isNullOrEmpty()) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
