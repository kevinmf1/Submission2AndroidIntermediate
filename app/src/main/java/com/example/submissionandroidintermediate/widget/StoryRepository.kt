package com.example.submissionandroidintermediate.widget

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.paging.PagingData
import com.example.submissionandroidintermediate.dataclass.ResponseDetail
import java.io.File

interface StoryRepository {

    fun getStory(token: String): LiveData<PagingData<StoryItemEntity>>

    fun getStoryWithLocation(token: String): LiveData<Result<List<StoryItemEntity>>>

    fun addStory(
        token: String,
        file: File,
        desc: String,
        location: Location?
    ): LiveData<Result<ResponseDetail>>
}