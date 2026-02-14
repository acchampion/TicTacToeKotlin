package com.wiley.fordummies.androidsdk.tictactoe.network

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.annotation.Keep
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.wiley.fordummies.androidsdk.tictactoe.R
import com.wiley.fordummies.androidsdk.tictactoe.TicTacToeApplication
import com.wiley.fordummies.androidsdk.tictactoe.api.PhotoInterceptor
import com.wiley.fordummies.androidsdk.tictactoe.model.GalleryItem
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import timber.log.Timber

@Keep
class FlickrFetchr {
	private val mPhotoInterceptor: PhotoInterceptor = PhotoInterceptor()
	private val mClient: OkHttpClient = OkHttpClient.Builder()
		.addInterceptor(mPhotoInterceptor)
		.build()
	private val mFlickrAccessToken = TicTacToeApplication.getContext().resources.getString(R.string.flickr_access_token)
	private val mFlickrApi: com.wiley.fordummies.androidsdk.tictactoe.api.FlickrApi
	private val mRetrofit: Retrofit = Retrofit.Builder()
		.baseUrl("https://api.flickr.com/")
		.addConverterFactory(MoshiConverterFactory.create())
		.client(mClient)
		.build()
	private val classTag = javaClass.simpleName

	init {
		mFlickrApi = mRetrofit.create(com.wiley.fordummies.androidsdk.tictactoe.api.FlickrApi::class.java)
	}

	fun fetchPhotosRequest(): Call<com.wiley.fordummies.androidsdk.tictactoe.api.FlickrResponse> {
		return mFlickrApi.fetchPhotos(mFlickrAccessToken)
	}

	fun fetchPhotosOld(): LiveData<List<GalleryItem>> {
		val responseLiveData = MutableLiveData<List<GalleryItem>>()
		val flickrRequest = mFlickrApi.fetchPhotos(mFlickrAccessToken)
		flickrRequest.enqueue(object : Callback<com.wiley.fordummies.androidsdk.tictactoe.api.FlickrResponse?> {
			override fun onResponse(
				call: Call<com.wiley.fordummies.androidsdk.tictactoe.api.FlickrResponse?>,
				response: Response<com.wiley.fordummies.androidsdk.tictactoe.api.FlickrResponse?>
			) {
				Timber.tag(classTag).d("Response received")
				val flickrResponse = response.body()
				if (flickrResponse != null) {
					val photoResponse = flickrResponse.photos
					val galleryItems = photoResponse.galleryItems
					val validGalleryItems: MutableList<GalleryItem> = ArrayList()
					for (item in galleryItems) {
						validGalleryItems.add(item)
					}
					responseLiveData.value = validGalleryItems
				}
			}

			override fun onFailure(call: Call<com.wiley.fordummies.androidsdk.tictactoe.api.FlickrResponse?>, t: Throwable) {
				Timber.tag(classTag).e(t, "Failed to fetch photos")
			}
		})
		return responseLiveData
	}

	@WorkerThread
	fun fetchPhoto(url: String?): Bitmap? {
		val response: Response<ResponseBody> = mFlickrApi.fetchUrlBytes(url).execute()
		val bitmap = response.body()?.byteStream()?.use(BitmapFactory::decodeStream)
		Timber.tag(classTag).i("Decoded bitmap $bitmap from Response $response")
		return bitmap
	}

	fun fetchPhotos(): LiveData<List<GalleryItem>> {
		// return fetchPhotoMetadata(mFlickrApi.fetchPhotos());
		return fetchPhotoMetadata(fetchPhotosRequest())
	}

	fun searchPhotosRequest(query: String?): Call<com.wiley.fordummies.androidsdk.tictactoe.api.FlickrResponse> {
		return mFlickrApi.searchPhotos(query!!)
	}

	fun searchPhotos(query: String?): LiveData<List<GalleryItem>> {
		// return fetchPhotoMetadata(mFlickrApi.searchPhotos(query));
		return fetchPhotoMetadata(searchPhotosRequest(query))
	}

	private fun fetchPhotoMetadata(flickrRequest: Call<com.wiley.fordummies.androidsdk.tictactoe.api.FlickrResponse>): LiveData<List<GalleryItem>> {
		val responseLiveData = MutableLiveData<List<GalleryItem>>()
		flickrRequest.enqueue(object : Callback<com.wiley.fordummies.androidsdk.tictactoe.api.FlickrResponse?> {
			override fun onResponse(
				call: Call<com.wiley.fordummies.androidsdk.tictactoe.api.FlickrResponse?>,
				response: Response<com.wiley.fordummies.androidsdk.tictactoe.api.FlickrResponse?>
			) {
				Timber.tag(classTag).d("Response received")
				val flickrResponse = response.body()
				if (flickrResponse != null) {
					val photoResponse = flickrResponse.photos
					val galleryItems = photoResponse.galleryItems
					val validGalleryItems: MutableList<GalleryItem> = ArrayList()
					validGalleryItems.addAll(galleryItems)
					responseLiveData.value = validGalleryItems
				}
			}

			override fun onFailure(call: Call<com.wiley.fordummies.androidsdk.tictactoe.api.FlickrResponse?>, t: Throwable) {
				Timber.tag(classTag).e(t, "Failed to fetch photos")
			}
		})
		return responseLiveData
	}
}
