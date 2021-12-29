package com.wiley.fordummies.androidsdk.tictactoe.api

import com.google.gson.annotations.SerializedName

class FlickrResponse {
    @SerializedName("photos")
    lateinit var photos: PhotoResponse

}
