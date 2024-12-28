package com.example.storyapp

import com.example.storyapp.data.remote.response.ApiResponse
import com.example.storyapp.data.remote.response.Story
import com.example.storyapp.data.remote.response.StoryResponse

object DataDummy {

    fun generateDummyStories(): List<Story> {
        val items: MutableList<Story> = arrayListOf()
        for (i in 0..100) {
            val story = Story(
                i.toString(),
                "name $i",
                "description $i",
                "photoUrl $i",
                "createdAt + $i",
                0.0,
                0.0,
            )
            items.add(story)
        }
        return items
    }

}