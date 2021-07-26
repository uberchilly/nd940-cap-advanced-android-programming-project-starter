package com.example.android.politicalpreparedness.network

import com.example.android.politicalpreparedness.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

class CivicsHttpClient : OkHttpClient() {

    companion object {

        const val API_KEY = "AIzaSyBvzQsmHHqAMHXYV20mF0KVaMJfcjvdOfs"

        fun getClient(): OkHttpClient {
            val logging = HttpLoggingInterceptor()
            logging.level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else
                HttpLoggingInterceptor.Level.NONE

            return Builder()
                .addInterceptor(logging)
                .addInterceptor { chain ->
                    val original = chain.request()
                    val url = original
                        .url
                        .newBuilder()
                        .addQueryParameter("key", API_KEY)
                        .build()
                    val request = original
                        .newBuilder()
                        .url(url)
                        .build()
                    chain.proceed(request)
                }
                .build()
        }

    }

}