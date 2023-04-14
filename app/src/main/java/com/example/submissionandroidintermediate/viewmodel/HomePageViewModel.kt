package com.example.submissionandroidintermediate.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.submissionandroidintermediate.api.APIConfig
import com.example.submissionandroidintermediate.dataclass.RegisterDataAccount
import com.example.submissionandroidintermediate.dataclass.ResponseDetail
import com.example.submissionandroidintermediate.dataclass.ResponseStory
import com.example.submissionandroidintermediate.dataclass.StoryDetail
import retrofit2.Call
import retrofit2.Response

class HomePageViewModel : ViewModel() {

    private val _listStory = MutableLiveData<List<StoryDetail>>()
    val listStory: LiveData<List<StoryDetail>> = _listStory

    private val _message = MutableLiveData<String>()
    val message: LiveData<String> = _message

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    var isError: Boolean = false

    fun getStories(token: String) {
        _isLoading.value = true
        val api = APIConfig.getApiService().getStory("Bearer $token")
        api.enqueue(object : retrofit2.Callback<ResponseStory> {
            override fun onResponse(call: Call<ResponseStory>, response: Response<ResponseStory>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    isError = false
                    val responseBody = response.body()
                    if (responseBody != null) {
                        _listStory.value = responseBody.listStory
                    }
                    _message.value = responseBody?.message.toString()

                } else {
                    isError = true
                    _message.value = response.message()
                }
            }
            override fun onFailure(call: Call<ResponseStory>, t: Throwable) {
                _isLoading.value = false
                isError = true
                _message.value = t.message.toString()
            }

        })
    }
}