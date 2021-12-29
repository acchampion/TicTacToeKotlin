package com.wiley.fordummies.androidsdk.tictactoe.api

import com.wiley.fordummies.androidsdk.tictactoe.BuildConfig
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

interface FlickrApi {
    @GET("/")
    fun fetchContents(): Call<String>

    @GET(
        "services/rest/?method=flickr.interestingness.getList" +
                "&api_key=" + BuildConfig.FlickrAccessToken +
                "&format=json" +
                "&nojsoncallback=1" +
                "&extras=url_s"
    )
    fun fetchPhotos(): Call<FlickrResponse>

    @GET
    fun fetchUrlBytes(@Url url: String?): Call<ResponseBody>

    @GET("services/rest/?method=flickr.photos.search")
    fun searchPhotos(@Query("text") query: String): Call<FlickrResponse>
}
