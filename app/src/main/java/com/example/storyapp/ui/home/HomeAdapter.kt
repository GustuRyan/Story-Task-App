package com.example.storyapp.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.storyapp.data.remote.response.Story
import com.example.storyapp.databinding.FragmentCardStoryBinding

class HomeAdapter(
    private var stories: List<Story>,
    private val onStoryClicked: (String) -> Unit
) : RecyclerView.Adapter<HomeAdapter.StoryViewHolder>() {

    fun updateData(newStories: List<Story>) {
        stories = newStories
        notifyDataSetChanged()
    }

    inner class StoryViewHolder(private val binding: FragmentCardStoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(story: Story) {
            with(binding) {
                tvItemName.text = story.name

                tvDate.text = if (story.createdAt.length >= 10) {
                    story.createdAt.substring(0, 10)
                } else {
                    story.createdAt
                }

                tvDescriptionStory.text = story.description

                Glide.with(root.context)
                    .load(story.photoUrl)
                    .into(ivItemPhoto)

                root.setOnClickListener {
                    onStoryClicked(story.id)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val binding = FragmentCardStoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return StoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        holder.bind(stories[position])
    }

    override fun getItemCount(): Int = stories.size
}


