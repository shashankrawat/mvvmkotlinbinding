package com.mvvmkotlinbinding.data.network;


import androidx.annotation.NonNull;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.concurrent.TimeUnit;

public class CallServer {
    private static CallServer instance;

    private ApiUtils utils;
    public static String serverError = "Server could not reach, please try again later.";


    /**
     * Constructor
     */
    private CallServer() {
        buildRetrofitServices();
    }

    /**
     * @return The instance of this Singleton
     */
    public static CallServer get() {
        if (instance == null) {
            synchronized (CallServer.class) {
                if (instance == null) {
                    instance = new CallServer();
                }
            }
        }
        return instance;
    }

    private void buildRetrofitServices() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient
                .Builder()
                .connectTimeout(20, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .addInterceptor(interceptor).build();

        this.utils = new Retrofit.Builder()
                .baseUrl(ApiUtils.URL_MASTER)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
                .create(ApiUtils.class);
    }

    @NonNull
    public ApiUtils getAPIName() {
        return utils;
    }
}
