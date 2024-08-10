package com.wiley.fordummies.androidsdk.tictactoe.model.viewmodel

import android.app.Application
import androidx.annotation.Keep
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.wiley.fordummies.androidsdk.tictactoe.TicTacToeApplication
import com.wiley.fordummies.androidsdk.tictactoe.model.GalleryItem
import com.wiley.fordummies.androidsdk.tictactoe.model.Settings
import com.wiley.fordummies.androidsdk.tictactoe.model.SettingsDataStore
import com.wiley.fordummies.androidsdk.tictactoe.network.FlickrFetchr
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Keep
class PhotoGalleryViewModel(private val app: Application) : AndroidViewModel(app) {
	val galleryItemLiveData: LiveData<List<GalleryItem>>
	private val mFlickrFetchr = FlickrFetchr()
	private val mDataStore = TicTacToeApplication.getDataStore()
	private val mMutableSearchTerm = MutableLiveData<String>()
	val searchTerm: String
		get() = mMutableSearchTerm.value ?: ""

	@JvmOverloads
	fun fetchPhotos(query: String = "") {
		CoroutineScope(Dispatchers.IO).launch {
			mDataStore.putString(Settings.Keys.PREF_SEARCH_QUERY, "")
		}
		mMutableSearchTerm.value = query
	}

	init {
		CoroutineScope(Dispatchers.IO).launch {
			mMutableSearchTerm.postValue(mDataStore.getString(Settings.Keys.PREF_SEARCH_QUERY, ""))
		}

		// mGalleryItemLiveData = flickrFetchr.fetchPhotosOld();
		if (searchTerm.isEmpty()) {
			galleryItemLiveData = mFlickrFetchr.fetchPhotos()
		} else {
			galleryItemLiveData = mFlickrFetchr.searchPhotos(searchTerm)
		}
	}
}
