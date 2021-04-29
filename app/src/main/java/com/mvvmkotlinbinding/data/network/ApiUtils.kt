package com.mvvmkotlinbinding.data.network

import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiUtils {
    @POST("loginFB")
    fun fbLogin(@Body obj: JsonObject?): Call<JsonObject?>?

    @POST("loginINSTA")
    fun instaLogin(@Body obj: JsonObject?): Call<JsonObject?>?

    companion object {
        const val HOST = "http://3.218.105.239:3000/"
        const val URL_MASTER = HOST + "api/"
    }
}