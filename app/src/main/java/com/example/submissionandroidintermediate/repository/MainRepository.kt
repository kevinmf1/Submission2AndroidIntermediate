package com.example.submissionandroidintermediate.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.*
import com.example.submissionandroidintermediate.api.APIConfig
import com.example.submissionandroidintermediate.api.APIService
import com.example.submissionandroidintermediate.database.ListStoryDetail
import com.example.submissionandroidintermediate.database.StoryDatabase
import com.example.submissionandroidintermediate.dataclass.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainRepository(
    private val storyDatabase: StoryDatabase,
    private val apiService: APIService
) {
    private var _storiesWithLocation = MutableLiveData<List<ListStoryDetail>>()
    var storiesWithLocation: LiveData<List<ListStoryDetail>> = _storiesWithLocation

    private val _message = MutableLiveData<String>()
    val message: LiveData<String> = _message

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _userLogin = MutableLiveData<ResponseLogin>()
    var userlogin: LiveData<ResponseLogin> = _userLogin

    fun getResponseLogin(loginDataAccount: LoginDataAccount) {
        _isLoading.value = true
        val api = APIConfig.getApiService().loginUser(loginDataAccount)
        api.enqueue(object : Callback<ResponseLogin> {
            override fun onResponse(call: Call<ResponseLogin>, response: Response<ResponseLogin>) {
                _isLoading.value = false
                val responseBody = response.body()

                if (response.isSuccessful) {
                    _userLogin.value = responseBody!!
                    _message.value = "Hello ${_userLogin.value!!.loginResult.name}!"
                } else {
                    when (response.code()) {
                        401 -> _message.value =
                            "Email atau password yang anda masukan salah, silahkan coba lagi"
                        408 -> _message.value =
                            "Koneksi internet anda lambat, silahkan coba lagi"
                        else -> _message.value = "Pesan error: " + response.message()
                    }
                }
            }

            override fun onFailure(call: Call<ResponseLogin>, t: Throwable) {
                _isLoading.value = false
                _message.value = "Pesan error: " + t.message.toString()
            }

        })
    }

    fun getResponseRegister(registDataUser: RegisterDataAccount) {
        _isLoading.value = true
        val api = APIConfig.getApiService().registUser(registDataUser)
        api.enqueue(object : Callback<ResponseDetail> {
            override fun onResponse(
                call: Call<ResponseDetail>,
                response: Response<ResponseDetail>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _message.value = "Yeay akun berhasil dibuat"
                } else {
                    when (response.code()) {
                        400 -> _message.value =
                            "Email yang anda masukan sudah terdaftar, silahkan coba lagi"
                        408 -> _message.value =
                            "Koneksi internet anda lambat, silahkan coba lagi"
                        else -> _message.value = "Pesan error: " + response.message()
                    }
                }
            }

            override fun onFailure(call: Call<ResponseDetail>, t: Throwable) {
                _isLoading.value = false
                _message.value = "Pesan error: " + t.message.toString()
            }

        })
    }

    fun upload(
        photo: MultipartBody.Part,
        des: RequestBody,
        lat: Double?,
        lng: Double?,
        token: String
    ) {
        _isLoading.value = true
        val service = APIConfig.getApiService().addStory(
            photo, des, lat?.toFloat(), lng?.toFloat(), "Bearer $token"
        )
        service.enqueue(object : Callback<ResponseDetail> {
            override fun onResponse(
                call: Call<ResponseDetail>,
                response: Response<ResponseDetail>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null && !responseBody.error) {
                        _message.value = responseBody.message
                    }
                } else {
                    _message.value = response.message()
                }
            }

            override fun onFailure(call: Call<ResponseDetail>, t: Throwable) {
                _isLoading.value = false
                _message.value = t.message
            }
        })
    }

    fun getStoriesWithLocation(token: String) {
        _isLoading.value = true
        val api = APIConfig.getApiService().getLocationStory(32, 1, "Bearer $token")
        api.enqueue(object : Callback<ResponseLocationStory> {
            override fun onResponse(
                call: Call<ResponseLocationStory>,
                response: Response<ResponseLocationStory>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        _storiesWithLocation.value = responseBody.listStory
                    }
                    _message.value = responseBody?.message.toString()

                } else {
                    _message.value = response.message()
                }
            }

            override fun onFailure(call: Call<ResponseLocationStory>, t: Throwable) {
                _isLoading.value = false
                _message.value = t.message.toString()
            }
        })
    }

    @ExperimentalPagingApi
    fun getPagingStories(token: String): LiveData<PagingData<ListStoryDetail>> {
        val pager = Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            remoteMediator = StoryRemoteMediator(storyDatabase, apiService, token),
            pagingSourceFactory = {
                storyDatabase.getListStoryDetailDao().getAllStories()
            }
        )
        return pager.liveData
    }

}