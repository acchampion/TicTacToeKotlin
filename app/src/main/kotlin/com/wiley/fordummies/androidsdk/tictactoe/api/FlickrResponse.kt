package com.wiley.fordummies.androidsdk.tictactoe.api

import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
@Keep
class FlickrResponse {
    @Json(name = "photos")
    lateinit var photos: com.wiley.fordummies.androidsdk.tictactoe.api.PhotoResponse

}
