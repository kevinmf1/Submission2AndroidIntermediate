package com.example.submissionandroidintermediate.unit.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.*
import androidx.recyclerview.widget.ListUpdateCallback
import com.example.submissionandroidintermediate.adapter.StoryListAdapter
import com.example.submissionandroidintermediate.database.ListStoryDetail
import com.example.submissionandroidintermediate.dataclass.ResponseLogin
import com.example.submissionandroidintermediate.repository.MainRepository
import com.example.submissionandroidintermediate.utils.DataDummy.generateDummyNewStories
import com.example.submissionandroidintermediate.utils.DataDummy.generateDummyNewsEntity
import com.example.submissionandroidintermediate.utils.DataDummy.generateDummyRequestLogin
import com.example.submissionandroidintermediate.utils.DataDummy.generateDummyRequestRegister
import com.example.submissionandroidintermediate.utils.DataDummy.generateDummyResponseLogin
import com.example.submissionandroidintermediate.utils.MainDispatcherRule
import com.example.submissionandroidintermediate.utils.getOrAwaitValue
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import java.io.File

class MainRepositoryTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainDispatcherRule = MainDispatcherRule()

    private lateinit var mainRepository: MainRepository

    @Mock
    private var mockFile = File("fileName")

    @Before
    fun setUp() {
        mainRepository = Mockito.mock(MainRepository::class.java)
    }

    @Test
    fun `verify getResponseLogin function is working`() {
        val dummyRequestLogin = generateDummyRequestLogin()
        val dummyResponseLogin = generateDummyResponseLogin()

        val expectedResponseLogin = MutableLiveData<ResponseLogin>()
        expectedResponseLogin.value = dummyResponseLogin

        mainRepository.getResponseLogin(dummyRequestLogin)

        Mockito.verify(mainRepository).getResponseLogin(dummyRequestLogin)
        `when`(mainRepository.userlogin).thenReturn(expectedResponseLogin)

        val actualData = mainRepository.userlogin.getOrAwaitValue()
        Mockito.verify(mainRepository).userlogin
        assertNotNull(actualData)
        assertEquals(expectedResponseLogin.value, actualData)
    }

    @Test
    fun `when login should return the right login response and not null`() {
        val dummyResponselogin = generateDummyResponseLogin()

        val expectedLogin = MutableLiveData<ResponseLogin>()
        expectedLogin.value = dummyResponselogin

        `when`(mainRepository.userlogin).thenReturn(expectedLogin)
        val actualLoginResponse = mainRepository.userlogin.getOrAwaitValue()

        Mockito.verify(mainRepository).userlogin
        assertNotNull(actualLoginResponse)
        assertEquals(expectedLogin.value, actualLoginResponse)
    }

    @Test
    fun `verify getResponseRegister function is working`() {
        val dummyRequestRegister = generateDummyRequestRegister()
        val expectedRegisterMessage = MutableLiveData<String>()
        expectedRegisterMessage.value = "User Created"

        mainRepository.getResponseRegister(dummyRequestRegister)

        Mockito.verify(mainRepository).getResponseRegister(dummyRequestRegister)
        `when`(mainRepository.message).thenReturn(expectedRegisterMessage)

        val actualData = mainRepository.message.getOrAwaitValue()

        Mockito.verify(mainRepository).message
        assertNotNull(actualData)
        assertEquals(expectedRegisterMessage.value, actualData)
    }

    @Test
    fun `verify upload function is working`() {
        val expectedRegisterMessage = MutableLiveData<String>()
        expectedRegisterMessage.value = "Story Uploaded"

        val requestImageFile = mockFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
            "photo",
            "fileName",
            requestImageFile
        )
        val description: RequestBody = "ini description".toRequestBody("text/plain".toMediaType())
        val token = "ini token"
        val latlng = LatLng(1.1, 1.1)

        mainRepository.upload(
            imageMultipart,
            description,
            latlng.latitude,
            latlng.longitude,
            token
        )

        Mockito.verify(mainRepository).upload(
            imageMultipart,
            description,
            latlng.latitude,
            latlng.longitude,
            token
        )

        `when`(mainRepository.message).thenReturn(expectedRegisterMessage)

        val actualRegisterMessage = mainRepository.message.getOrAwaitValue()

        Mockito.verify(mainRepository).message
        assertNotNull(actualRegisterMessage)
        assertEquals(expectedRegisterMessage.value, actualRegisterMessage)
    }

    @Test
    fun `verify getStories function is working`() {
        val dummyStories = generateDummyNewsEntity()
        val expectedStories = MutableLiveData<List<ListStoryDetail>>()
        expectedStories.value = dummyStories

        val token = "ini token"
        mainRepository.getStories(token)
        Mockito.verify(mainRepository).getStories(token)

        `when`(mainRepository.stories).thenReturn(expectedStories)

        val actualStories = mainRepository.stories.getOrAwaitValue()

        Mockito.verify(mainRepository).stories

        assertNotNull(actualStories)
        assertEquals(expectedStories.value, actualStories)
        assertEquals(dummyStories.size, actualStories.size)
    }

    @Test
    fun `when stories should return the right data and not null`() {
        val dummyStories = generateDummyNewsEntity()
        val expectedStories = MutableLiveData<List<ListStoryDetail>>()
        expectedStories.value = dummyStories

        `when`(mainRepository.stories).thenReturn(expectedStories)

        val actualStories = mainRepository.stories.getOrAwaitValue()

        Mockito.verify(mainRepository).stories

        assertNotNull(actualStories)
        assertEquals(expectedStories.value, actualStories)
        assertEquals(dummyStories.size, actualStories.size)
    }

    @Test
    fun `when message should return the right data and not null`() {
        val expectedRegisterMessage = MutableLiveData<String>()
        expectedRegisterMessage.value = "Story Uploaded"

        `when`(mainRepository.message).thenReturn(expectedRegisterMessage)

        val actualRegisterMessage = mainRepository.message.getOrAwaitValue()

        Mockito.verify(mainRepository).message
        assertNotNull(actualRegisterMessage)
        assertEquals(expectedRegisterMessage.value, actualRegisterMessage)
    }

    @Test
    fun `when loading state should return the right data and not null`() {
        val expectedLoadingData = MutableLiveData<Boolean>()
        expectedLoadingData.value = true

        `when`(mainRepository.isLoading).thenReturn(expectedLoadingData)

        val actualLoading = mainRepository.isLoading.getOrAwaitValue()

        Mockito.verify(mainRepository).isLoading
        assertNotNull(actualLoading)
        assertEquals(expectedLoadingData.value, actualLoading)
    }

    @ExperimentalCoroutinesApi
    @ExperimentalPagingApi
    @Test
    fun `verify getPagingStory function is working and should not null`() = runTest {
        val noopListUpdateCallback = NoopListCallback()
        val dummyStory = generateDummyNewStories()
        val data = PagedTestDataSources.snapshot(dummyStory)
        val story = MutableLiveData<PagingData<ListStoryDetail>>()
        val token = "ini token"
        story.value = data

        `when`(mainRepository.getPagingStories(token)).thenReturn(story)

        val actualData = mainRepository.getPagingStories(token).getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryListAdapter.StoryDetailDiffCallback(),
            updateCallback = noopListUpdateCallback,
            mainDispatcher = Dispatchers.Unconfined,
            workerDispatcher = Dispatchers.Unconfined,
        )
        differ.submitData(actualData)


        advanceUntilIdle()
        Mockito.verify(mainRepository).getPagingStories(token)
        assertNotNull(differ.snapshot())
        assertEquals(dummyStory.size, differ.snapshot().size)
        assertEquals(dummyStory[0].name, differ.snapshot()[0]?.name)
    }

    class NoopListCallback : ListUpdateCallback {
        override fun onChanged(position: Int, count: Int, payload: Any?) {}
        override fun onMoved(fromPosition: Int, toPosition: Int) {}
        override fun onInserted(position: Int, count: Int) {}
        override fun onRemoved(position: Int, count: Int) {}
    }

    class PagedTestDataSources private constructor() :
        PagingSource<Int, LiveData<List<ListStoryDetail>>>() {
        companion object {
            fun snapshot(items: List<ListStoryDetail>): PagingData<ListStoryDetail> {
                return PagingData.from(items)
            }
        }

        override fun getRefreshKey(state: PagingState<Int, LiveData<List<ListStoryDetail>>>): Int {
            return 0
        }

        override suspend fun load(params: LoadParams<Int>): LoadResult<Int, LiveData<List<ListStoryDetail>>> {
            return LoadResult.Page(emptyList(), 0, 1)
        }
    }
}