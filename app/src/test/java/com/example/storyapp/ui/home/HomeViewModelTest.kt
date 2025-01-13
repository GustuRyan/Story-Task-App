package com.example.storyapp.ui.home

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.recyclerview.widget.ListUpdateCallback
import com.example.storyapp.DataDummy
import com.example.storyapp.MainDispatcherRule
import com.example.storyapp.data.remote.response.Story
import com.example.storyapp.data.repository.StoryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Rule
import org.junit.Test // Menggunakan JUnit 4 @Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class HomeViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock
    private lateinit var storyRepository: StoryRepository

    @Test
    fun `when Get Stories Should Not Null and Return Data`() = runTest {
        // Arrange
        val dummyStories = DataDummy.generateDummyStories()
        val data: PagingData<Story> = StoryPagingSource.snapshot(dummyStories)
        val flowData = flowOf(data) // Menggunakan flowOf() untuk menghasilkan Flow<PagingData<Story>>

        // Mock repository
        Mockito.`when`(storyRepository.getStories()).thenReturn(flowData)

        val homeViewModel = HomeViewModel(storyRepository)

        // Act
        val actualStories = homeViewModel.stories.first() // Ambil data pertama dari Flow

        val differ = AsyncPagingDataDiffer(
            diffCallback = HomeAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main
        )
        differ.submitData(actualStories)

        // Assert
        Assert.assertNotNull(differ.snapshot())
        Assert.assertEquals(dummyStories.size, differ.snapshot().size)
        Assert.assertEquals(dummyStories[0], differ.snapshot()[0])
    }

    @Test
    fun `when Get Stories Empty Should Return No Data`() = runTest {
        // Arrange
        val emptyStories = emptyList<Story>()
        val data: PagingData<Story> = StoryPagingSource.snapshot(emptyStories)
        val flowData = flowOf(data)

        // Mock repository
        Mockito.`when`(storyRepository.getStories()).thenReturn(flowData)

        val homeViewModel = HomeViewModel(storyRepository)

        // Act
        val actualStories = homeViewModel.stories.first() // Ambil data pertama dari Flow

        val differ = AsyncPagingDataDiffer(
            diffCallback = HomeAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main
        )
        differ.submitData(actualStories)

        // Assert
        Assert.assertNotNull(differ.snapshot())
        Assert.assertTrue(differ.snapshot().isEmpty())
    }
}

class StoryPagingSource : PagingSource<Int, LiveData<List<Story>>>() {
    companion object {
        fun snapshot(items: List<Story>): PagingData<Story> {
            return PagingData.from(items)
        }
    }
    override fun getRefreshKey(state: PagingState<Int, LiveData<List<Story>>>): Int {
        return 0
    }
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, LiveData<List<Story>>> {
        return LoadResult.Page(emptyList(), 0, 1)
    }
}

val noopListUpdateCallback = object : ListUpdateCallback {
    override fun onInserted(position: Int, count: Int) {}
    override fun onRemoved(position: Int, count: Int) {}
    override fun onMoved(fromPosition: Int, toPosition: Int) {}
    override fun onChanged(position: Int, count: Int, payload: Any?) {}
}