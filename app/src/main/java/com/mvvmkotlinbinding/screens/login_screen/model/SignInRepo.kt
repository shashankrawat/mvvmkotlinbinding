package com.mvvmkotlinbinding.screens.login_screen.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonObject
import com.mvvmkotlinbinding.app_common_components.app_abstracts.BaseRepo
import com.mvvmkotlinbinding.data.data_beans.LoginBean
import com.mvvmkotlinbinding.data.network.CallServer
import com.mvvmkotlinbinding.data.network.Resource
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignInRepo : BaseRepo()
{
    companion object {
        @JvmStatic
        fun get(): SignInRepo {
            return SignInRepo()
        }
    }

    fun FBSignIn(obj: JsonObject?): LiveData<Resource<String>> {
        val data = MutableLiveData<Resource<String>>()
        data.value = Resource.loading(null)
        CallServer.get()?.aPIName?.fbLogin(obj)?.enqueue(object : Callback<JsonObject?> {
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                if (response.isSuccessful) {
                    if (response.body() != null) {
                        if (response.body()!!["error"].asString.equals(
                                "false",
                                ignoreCase = true
                            )
                        ) {
    //                            UserSessionImpl.getInstance().saveUserData(response.body().get("userInfo").getAsJsonObject().toString());
    //                            UserSessionImpl.getInstance().saveUserToken(response.body().get("token").getAsString());
                            data.setValue(
                                Resource.success(
                                    response.body()!!["message"].asString
                                )
                            )
                        } else data.setValue(
                            Resource.error(
                                response.body()!!["message"].asString, null, 0, null
                            )
                        )
                    }
                } else data.setValue(Resource.error(response.message(), null, 0, null))
            }

            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                data.value = Resource.error(CallServer.serverError, null, 0, t)
            }
        })
        return data
    }

    fun InstaSignIn(obj: JsonObject?): LiveData<Resource<String>> {
        val data = MutableLiveData<Resource<String>>()
        data.value = Resource.loading(null)
        CallServer.get()?.aPIName?.instaLogin(obj)?.enqueue(object : Callback<JsonObject?> {
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                if (response.isSuccessful) {
                    if (response.body() != null) {
                        if (response.body()!!["error"].asString.equals(
                                "false",
                                ignoreCase = true
                            )
                        ) {
                            data.setValue(
                                Resource.success(
                                    response.body()!!["message"].asString
                                )
                            )
                        } else data.setValue(

                            Resource.error(
                                response.body()!!["message"].asString, null, 0, null
                            )
                        )
                    }
                } else data.setValue(Resource.error(response.message(), null, 0, null))
            }

            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                data.value = Resource.error(CallServer.serverError, null, 0, t)
            }
        })
        return data
    }

    fun signInUser(obj: LoginBean?): LiveData<Resource<LoginBean>> {
        val data = MutableLiveData<Resource<LoginBean>>()
        data.value = Resource.loading(null)
        data.value = Resource.success(obj)
        return data
    }

}