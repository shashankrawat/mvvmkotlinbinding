package com.mvvmkotlinbinding.data.network

import com.mvvmkotlinbinding.data.network.ApiUtils
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class CallServer private constructor() {
    private var utils: ApiUtils? = null
    private fun buildRetrofitServices() {
        val interceptor = HttpLoggingInterceptor()
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)

        val client: OkHttpClient = OkHttpClient().newBuilder()
            .connectTimeout(20, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .addInterceptor(interceptor).build()

        utils = Retrofit.Builder()
            .baseUrl(ApiUtils.URL_MASTER)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(ApiUtils::class.java)
    }

    val aPIName: ApiUtils
        get() = utils!!

    companion object {
        private var instance: CallServer? = null
        var serverError = "Server could not reach, please try again later."

        /**
         * @return The instance of this Singleton
         */
        fun get(): CallServer? {
            if (instance == null) {
                synchronized(CallServer::class.java) {
                    if (instance == null) {
                        instance = CallServer()
                    }
                }
            }
            return instance
        }
    }

    /**
     * Constructor
     */
    init {
        buildRetrofitServices()
    }
}