package com.wiley.fordummies.androidsdk.tictactoe.api

import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.wiley.fordummies.androidsdk.tictactoe.model.GalleryItem

@Keep
@JsonClass(generateAdapter = true)
class PhotoResponse {
    @Json(name = "photo")
    lateinit var galleryItems: List<GalleryItem>

}
