package com.example.submissionandroidintermediate.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.submissionandroidintermediate.dataclass.LoginDataAccount
import com.example.submissionandroidintermediate.dataclass.ResponseLogin
import com.example.submissionandroidintermediate.api.APIConfig
import com.example.submissionandroidintermediate.dataclass.RegisterDataAccount
import com.example.submissionandroidintermediate.dataclass.ResponseDetail
import retrofit2.Call
import retrofit2.Response

class MainViewModel : ViewModel() {
    private val _isLoadingLogin = MutableLiveData<Boolean>()
    val isLoadingLogin: LiveData<Boolean> = _isLoadingLogin
    var isErrorLogin: Boolean = false
    private val _messageLogin = MutableLiveData<String>()
    val messageLogin: LiveData<String> = _messageLogin
    private val _userLogin = MutableLiveData<ResponseLogin>()
    val userLogin: LiveData<ResponseLogin> = _userLogin

    var isErrorRegist: Boolean = false
    private val _isLoadingRegist = MutableLiveData<Boolean>()
    val isLoadingRegist: LiveData<Boolean> = _isLoadingRegist
    private val _messageRegist = MutableLiveData<String>()
    val messageRegist: LiveData<String> = _messageRegist

    fun getResponseLogin(loginDataAccount: LoginDataAccount) {
        _isLoadingLogin.value = true
        val api = APIConfig.getApiService().loginUser(loginDataAccount)
        api.enqueue(object : retrofit2.Callback<ResponseLogin> {
            override fun onResponse(call: Call<ResponseLogin>, response: Response<ResponseLogin>) {
                _isLoadingLogin.value = false
                val responseBody = response.body()

                if (response.isSuccessful) {
                    isErrorLogin = false
                    _userLogin.value = responseBody!!
                    _messageLogin.value = "Halo ${_userLogin.value!!.loginResult.name}!"
                } else {
                    isErrorLogin = true
                    when (response.code()) {
                        401 -> _messageLogin.value =
                            "Email atau password yang anda masukan salah, silahkan coba lagi"
                        408 -> _messageLogin.value =
                            "Koneksi internet anda lambat, silahkan coba lagi"
                        else -> _messageLogin.value = "Pesan error: " + response.message()
                    }
                }
            }

            override fun onFailure(call: Call<ResponseLogin>, t: Throwable) {
                isErrorLogin = true
                _isLoadingLogin.value = false
                _messageLogin.value = "Pesan error: " + t.message.toString()
            }

        })
    }

    fun getResponseRegister(registDataUser: RegisterDataAccount) {
        _isLoadingRegist.value = true
        val api = APIConfig.getApiService().registUser(registDataUser)
        api.enqueue(object : retrofit2.Callback<ResponseDetail> {
            override fun onResponse(
                call: Call<ResponseDetail>,
                response: Response<ResponseDetail>
            ) {
                _isLoadingRegist.value = false
                if (response.isSuccessful) {
                    isErrorRegist = false
                    _messageRegist.value = "Yeay akun berhasil dibuat"
                } else {
                    isErrorRegist = true
                    when (response.code()) {
                        400 -> _messageRegist.value =
                            "Email yang anda masukan sudah terdaftar, silahkan ganti email"
                        408 -> _messageRegist.value =
                            "Koneksi internet anda lambat, silahkan coba lagi"
                        else -> _messageRegist.value = "Pesan error: " + response.message()
                    }
                }
            }

            override fun onFailure(call: Call<ResponseDetail>, t: Throwable) {
                isErrorRegist = true
                _isLoadingRegist.value = false
                _messageRegist.value = "Pesan error: " + t.message.toString()
            }

        })
    }
}