package com.wiley.fordummies.androidsdk.tictactoe.network

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.wiley.fordummies.androidsdk.tictactoe.api.FlickrApi
import com.wiley.fordummies.androidsdk.tictactoe.api.FlickrResponse
import com.wiley.fordummies.androidsdk.tictactoe.api.PhotoInterceptor
import com.wiley.fordummies.androidsdk.tictactoe.model.GalleryItem
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import java.util.*

class FlickrFetchr {
	private val mPhotoInterceptor: PhotoInterceptor
	private val mClient: OkHttpClient
	private val mFlickrApi: FlickrApi
	private val mRetrofit: Retrofit
	private val TAG = javaClass.simpleName

	init {
		mPhotoInterceptor = PhotoInterceptor()
		mClient = OkHttpClient.Builder()
			.addInterceptor(mPhotoInterceptor)
			.build()
		mRetrofit = Retrofit.Builder()
			.baseUrl("https://api.flickr.com/")
			.addConverterFactory(GsonConverterFactory.create())
			.client(mClient)
			.build()
		mFlickrApi = mRetrofit.create(FlickrApi::class.java)
	}

	fun fetchPhotosRequest(): Call<FlickrResponse> {
		return mFlickrApi.fetchPhotos()
	}

	fun fetchPhotosOld(): LiveData<List<GalleryItem>> {
		val responseLiveData = MutableLiveData<List<GalleryItem>>()
		val flickrRequest = mFlickrApi.fetchPhotos()
		flickrRequest.enqueue(object : Callback<FlickrResponse?> {
			override fun onResponse(
				call: Call<FlickrResponse?>,
				response: Response<FlickrResponse?>
			) {
				Timber.tag(TAG).d("Response received")
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

			override fun onFailure(call: Call<FlickrResponse?>, t: Throwable) {
				Timber.tag(TAG).e(t, "Failed to fetch photos")
			}
		})
		return responseLiveData
	}

	@WorkerThread
	fun fetchPhoto(url: String?): Bitmap? {
		val response: Response<ResponseBody> = mFlickrApi.fetchUrlBytes(url).execute()
		val bitmap = response.body()?.byteStream()?.use(BitmapFactory::decodeStream)
		Timber.tag(TAG).i("Decoded bitmap $bitmap from Response $response")
		return bitmap
	}

	fun fetchPhotos(): LiveData<List<GalleryItem>> {
		// return fetchPhotoMetadata(mFlickrApi.fetchPhotos());
		return fetchPhotoMetadata(fetchPhotosRequest())
	}

	fun searchPhotosRequest(query: String?): Call<FlickrResponse> {
		return mFlickrApi.searchPhotos(query!!)
	}

	fun searchPhotos(query: String?): LiveData<List<GalleryItem>> {
		// return fetchPhotoMetadata(mFlickrApi.searchPhotos(query));
		return fetchPhotoMetadata(searchPhotosRequest(query))
	}

	private fun fetchPhotoMetadata(flickrRequest: Call<FlickrResponse>): LiveData<List<GalleryItem>> {
		val responseLiveData = MutableLiveData<List<GalleryItem>>()
		flickrRequest.enqueue(object : Callback<FlickrResponse?> {
			override fun onResponse(
				call: Call<FlickrResponse?>,
				response: Response<FlickrResponse?>
			) {
				Timber.tag(TAG).d("Response received")
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

			override fun onFailure(call: Call<FlickrResponse?>, t: Throwable) {
				Timber.tag(TAG).e(t, "Failed to fetch photos")
			}
		})
		return responseLiveData
	}
}
