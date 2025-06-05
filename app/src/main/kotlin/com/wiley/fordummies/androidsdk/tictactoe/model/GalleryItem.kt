package com.wiley.fordummies.androidsdk.tictactoe.model

import android.net.Uri
import androidx.annotation.Keep
import androidx.core.net.toUri
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
@Keep
data class GalleryItem(
	@Json(name = "title") var title: String = "",
	@Json(name = "id") var id: String = "",
	@Json(name = "url_s") var url: String = "",
	@Json(name = "owner") var owner: String = "") {

	val photoPageUri: Uri
        get() = "https://www.flickr.com/photos/".toUri()
            .buildUpon()
            .appendPath(owner)
            .appendPath(id)
            .build()

}
