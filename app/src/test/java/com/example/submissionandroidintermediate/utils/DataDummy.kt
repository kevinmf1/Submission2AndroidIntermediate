package com.example.submissionandroidintermediate.utils

import com.example.submissionandroidintermediate.database.ListStoryDetail
import com.example.submissionandroidintermediate.dataclass.*

object DataDummy {

    fun generateDummyNewsEntity(): List<ListStoryDetail> {
        val newsList = ArrayList<ListStoryDetail>()
        for (i in 0..5) {
            val stories = ListStoryDetail(
                "Title $i",
                "this is name",
                "This is description",
                "https://dicoding-web-img.sgp1.cdn.digitaloceanspaces.com/original/commons/feature-1-kurikulum-global-3.png",
                "2022-02-22T22:22:22Z",
                null,
                null,
            )
            newsList.add(stories)
        }
        return newsList
    }

    fun generateDummyNewStories(): List<ListStoryDetail> {
        val newsList = ArrayList<ListStoryDetail>()
        for (i in 0..5) {
            val stories = ListStoryDetail(
                "Title $i",
                "this is name",
                "This is description",
                "https://dicoding-web-img.sgp1.cdn.digitaloceanspaces.com/original/commons/feature-1-kurikulum-global-3.png",
                "2022-02-22T22:22:22Z",
                null,
                null,
            )
            newsList.add(stories)
        }
        return newsList
    }


    fun generateDummyRequestLogin(): LoginDataAccount {
        return LoginDataAccount("kopret1@gmail.com", "12345678")
    }

    fun generateDummyResponseLogin(): ResponseLogin {
        val newLoginResult = LoginResult("qwerty", "kevin", "ini-token")
        return ResponseLogin(false, "Login successfully", newLoginResult)
    }

    fun generateDummyRequestRegister(): RegisterDataAccount {
        return RegisterDataAccount("kevin", "123@gmail.com", "12345678")
    }

}