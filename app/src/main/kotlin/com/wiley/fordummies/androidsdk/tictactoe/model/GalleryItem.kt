package com.wiley.fordummies.androidsdk.tictactoe.model

import android.net.Uri
import androidx.annotation.Keep
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
        get() = Uri.parse("https://www.flickr.com/photos/")
            .buildUpon()
            .appendPath(owner)
            .appendPath(id)
            .build()

}
