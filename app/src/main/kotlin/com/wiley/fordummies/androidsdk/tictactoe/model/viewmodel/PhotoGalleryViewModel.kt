package com.wiley.fordummies.androidsdk.tictactoe.model.viewmodel

import android.app.Application
import androidx.annotation.Keep
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.wiley.fordummies.androidsdk.tictactoe.model.GalleryItem
import com.wiley.fordummies.androidsdk.tictactoe.model.QueryPreferences
import com.wiley.fordummies.androidsdk.tictactoe.network.FlickrFetchr

@Keep
class PhotoGalleryViewModel(private val app: Application) : AndroidViewModel(app) {
    val galleryItemLiveData: LiveData<List<GalleryItem>>
    private val mFlickrFetchr = FlickrFetchr()
    private val mMutableSearchTerm = MutableLiveData<String>()
    val searchTerm: String
        get() = mMutableSearchTerm.value ?: ""

    @JvmOverloads
    fun fetchPhotos(query: String = "") {
        QueryPreferences.setStoredQuery(app, query)
        mMutableSearchTerm.value = query
    }

    init {
        mMutableSearchTerm.value = QueryPreferences.getStoredQuery(app)

        // mGalleryItemLiveData = flickrFetchr.fetchPhotosOld();
		if (searchTerm.isEmpty()) {
			galleryItemLiveData = mFlickrFetchr.fetchPhotos()
		} else {
			galleryItemLiveData = mFlickrFetchr.searchPhotos(searchTerm)
		}
    }
}
