package com.wiley.fordummies.androidsdk.tictactoe.model

import android.net.Uri
import com.google.gson.annotations.SerializedName

data class GalleryItem(
	@SerializedName("title") var title: String = "",
	@SerializedName("id") var id: String = "",
	@SerializedName("url_s") var url: String = "",
	@SerializedName(value="owner") var owner: String = "") {

	val photoPageUri: Uri
        get() = Uri.parse("https://www.flickr.com/photos/")
            .buildUpon()
            .appendPath(owner)
            .appendPath(id)
            .build()

}
