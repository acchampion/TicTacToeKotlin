package com.wiley.fordummies.androidsdk.tictactoe.model

import android.net.Uri
import androidx.annotation.Keep
import androidx.core.net.toUri
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
@Keep
data class GalleryItem(
    @Json(name = "id") var id: String = "",
    @Json(name = "owner") var owner: String = "",
    @Json(name = "secret") var secret: String = "",
    @Json(name = "server") var serverId: String = "",
    @Json(name = "farm") var farm: String = "",
    @Json(name = "title") var title: String = "",
    @Json(name = "ispublic") var ispublic: Int = 1,
    @Json(name = "isfriend") var isfriend: Int = 0,
    @Json(name = "isfamily") var isfamily: Int = 0
    ) {

	val photoUri: Uri
        get() = "https://live.staticflickr.com/".toUri()
            .buildUpon()
            .appendPath(serverId)
            .appendPath("${id}_${secret}.jpg")
            .build()

    val photoPageUri: Uri
        get() = "https://www.flickr.com/photos/".toUri()
            .buildUpon()
            .appendPath(owner)
            .appendPath(id)
            .build()
}
