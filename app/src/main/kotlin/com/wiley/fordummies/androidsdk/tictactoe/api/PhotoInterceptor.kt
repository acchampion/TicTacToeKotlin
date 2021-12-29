package com.wiley.fordummies.androidsdk.tictactoe.api

import com.wiley.fordummies.androidsdk.tictactoe.BuildConfig
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import timber.log.Timber

class PhotoInterceptor : Interceptor {
    private val API_KEY: String = BuildConfig.FlickrAccessToken
    private val TAG = javaClass.simpleName

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest: Request = chain.request()
        val newUrl: HttpUrl = originalRequest.url().newBuilder()
            .addQueryParameter("api_key", API_KEY)
            .addQueryParameter("format", "json")
            .addQueryParameter("nojsoncallback", "1")
            .addQueryParameter("extras", "url_s")
            .addQueryParameter("safesearch", "1")
            .build()
        Timber.tag(TAG).d("Generated new Url: %s", newUrl)
        val newRequest: Request = originalRequest.newBuilder()
            .url(newUrl)
            .build()
        Timber.tag(TAG).d("Generated new Request: %s", newRequest)
        return chain.proceed(newRequest)
    }
}
