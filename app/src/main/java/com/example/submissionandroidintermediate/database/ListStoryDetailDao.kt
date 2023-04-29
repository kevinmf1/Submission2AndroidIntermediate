package com.example.submissionandroidintermediate.database

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ListStoryDetailDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStory(stories: List<ListStoryDetail>)

    @Query("SELECT * FROM stories")
    fun getAllStories(): PagingSource<Int, ListStoryDetail>

    @Query("DELETE FROM stories")
    suspend fun deleteAll()
}