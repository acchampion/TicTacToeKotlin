package com.wiley.fordummies.androidsdk.tictactoe.api

import androidx.annotation.Keep
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

@Keep
interface FlickrApi {
    @GET("/")
    fun fetchContents(): Call<String>

    @GET(
        "services/rest/?method=flickr.interestingness.getList" +
                "&format=json" +
                "&nojsoncallback=1" +
                "&extras=url_s"
    )
    fun fetchPhotos(@Query("api_key") query: String): Call<FlickrResponse>

    @GET
    fun fetchUrlBytes(@Url url: String?): Call<ResponseBody>

    @GET("services/rest/?method=flickr.photos.search")
    fun searchPhotos(@Query("text") query: String): Call<FlickrResponse>
}
