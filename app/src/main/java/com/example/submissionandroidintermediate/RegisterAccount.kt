package com.example.submissionandroidintermediate

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

data class RegisterAccount(
    var name: String,
    var email: String,
    var password: String
)

data class LoginAccount(
    var email: String,
    var password: String
)

data class ResponseDetail(
    var error: Boolean,
    var message: String
)

data class ResponseLogin(
    var error: Boolean,
    var message: String,
    var loginResult: LoginResult
)

data class LoginResult(
    var userId: String,
    var name: String,
    var token: String
)

data class ResponseStory(
    var error: String,
    var message: String,
    var listStory: List<StoryDetail>
)

@Parcelize
data class StoryDetail(
    var id: String,
    var name: String,
    var description: String,
    var photoUrl: String,
    var createdAt: String,
    var lat: Double,
    var lon: Double
) : Parcelable
