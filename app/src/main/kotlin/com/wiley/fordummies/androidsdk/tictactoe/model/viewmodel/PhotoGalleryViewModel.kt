package com.wiley.fordummies.androidsdk.tictactoe.model.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.wiley.fordummies.androidsdk.tictactoe.model.GalleryItem
import com.wiley.fordummies.androidsdk.tictactoe.model.QueryPreferences
import com.wiley.fordummies.androidsdk.tictactoe.network.FlickrFetchr

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
        galleryItemLiveData = Transformations.switchMap(
            mMutableSearchTerm
        ) { searchTerm: String ->
            if (searchTerm.isEmpty()) {
				return@switchMap mFlickrFetchr.fetchPhotos()
            } else {
				return@switchMap mFlickrFetchr.searchPhotos(searchTerm)
            }
        }
    }
}
