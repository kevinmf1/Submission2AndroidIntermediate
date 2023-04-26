package com.example.submissionandroidintermediate.di

import android.content.Context
import com.example.submissionandroidintermediate.api.APIConfig
import com.example.submissionandroidintermediate.database.StoryDatabase
import com.example.submissionandroidintermediate.repository.MainRepository

object Injection {
    fun provideRepository(context: Context): MainRepository {
        val database = StoryDatabase.getDatabase(context)
        val apiService = APIConfig.getApiService()
        return MainRepository(database, apiService)
    }
}