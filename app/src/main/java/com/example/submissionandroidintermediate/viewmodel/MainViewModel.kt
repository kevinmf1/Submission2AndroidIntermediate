package com.example.submissionandroidintermediate.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.submissionandroidintermediate.database.ListStoryDetail
import com.example.submissionandroidintermediate.dataclass.LoginDataAccount
import com.example.submissionandroidintermediate.dataclass.RegisterDataAccount
import com.example.submissionandroidintermediate.dataclass.ResponseLogin
import com.example.submissionandroidintermediate.repository.MainRepository
import okhttp3.MultipartBody
import okhttp3.RequestBody

class MainViewModel(private val mainRepository: MainRepository) : ViewModel() {

    val storiesWithLocation: LiveData<List<ListStoryDetail>> = mainRepository.storiesWithLocation

    val message: LiveData<String> = mainRepository.message

    val isLoading: LiveData<Boolean> = mainRepository.isLoading

    val userlogin: LiveData<ResponseLogin> = mainRepository.userlogin

    fun login(loginDataAccount: LoginDataAccount) {
        mainRepository.getResponseLogin(loginDataAccount)
    }

    fun register(registDataUser: RegisterDataAccount) {
        mainRepository.getResponseRegister(registDataUser)
    }

    fun upload(
        photo: MultipartBody.Part,
        des: RequestBody,
        lat: Double?,
        lng: Double?,
        token: String
    ) {
        mainRepository.upload(photo, des, lat, lng, token)
    }

    @ExperimentalPagingApi
    fun getPagingStories(token: String): LiveData<PagingData<ListStoryDetail>> {
        return mainRepository.getPagingStories(token).cachedIn(viewModelScope)
    }

    fun getStoriesWithLocation(token: String) {
        mainRepository.getStoriesWithLocation(token)
    }
}
