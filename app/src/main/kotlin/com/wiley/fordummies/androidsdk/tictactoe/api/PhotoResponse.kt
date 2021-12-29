package com.wiley.fordummies.androidsdk.tictactoe.api

import com.google.gson.annotations.SerializedName
import com.wiley.fordummies.androidsdk.tictactoe.model.GalleryItem

class PhotoResponse {
    @SerializedName("photo")
    lateinit var galleryItems: List<GalleryItem>

}
