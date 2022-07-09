package com.wiley.fordummies.androidsdk.tictactoe.network

import android.graphics.Bitmap
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.os.Message
import androidx.annotation.NonNull
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import timber.log.Timber
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.collections.set

private const val TAG = "ThumbnailDownloader"
private const val MESSAGE_DOWNLOAD = 0

class ThumbnailDownloader<in T> (
	private val responseHandler: Handler,
	private val onThumbnailDownloaded: (T, Bitmap) -> Unit
) :
    HandlerThread(TAG) {

	val fragmentLifecycleObserver: LifecycleObserver =
		object : DefaultLifecycleObserver {
			override fun onCreate(@NonNull owner: LifecycleOwner) {
				super.onCreate(owner)
				Timber.tag(TAG).i("Starting background thread")
				if (!isAlive) {
					start()
				}
				looper
			}

			override fun onDestroy(owner: LifecycleOwner) {
				super.onDestroy(owner)
				Timber.tag(TAG).i("Destroying background thread")
				quit()
			}
		}

	val viewLifecycleObserver = object : DefaultLifecycleObserver {
		override fun onDestroy(owner: LifecycleOwner) {
			super.onDestroy(owner)
			Timber.tag(TAG).i("Clearing all requests from queue")
			mRequestHandler.removeMessages(MESSAGE_DOWNLOAD)
			mRequestMap.clear()
		}
	}

	private var mHasQuit = false
    private lateinit var mRequestHandler: Handler
    private lateinit var mThumbnailDownloadListener: ThumbnailDownloadListener<T>
    private val mRequestMap = ConcurrentHashMap<T, String>()
    private val mFlickrFetchr = FlickrFetchr()

    interface ThumbnailDownloadListener<T> {
        fun onThumbnailDownloaded(holder: T, bitmap: Bitmap?)
    }

	@Suppress("UNCHECKED_CAST")
    override fun onLooperPrepared() {
        super.onLooperPrepared()
        mRequestHandler = object : Handler(Looper.getMainLooper()) {
			override fun handleMessage(msg: Message) {
                super.handleMessage(msg)
                if (msg.what == MESSAGE_DOWNLOAD) {
                    val target = msg.obj as T
                    Timber.tag(TAG).i("Got a request for URL: ${mRequestMap[target]}")
                    handleRequest(target)
                }
            }
        }
    }

    fun queueThumbnail(target: T, url: String) {
        Timber.tag(TAG).i("Got a URL: %s", url)
        mRequestMap[target] = url
        mRequestHandler.obtainMessage(MESSAGE_DOWNLOAD, target).sendToTarget()
    }

    override fun quit(): Boolean {
        mHasQuit = true
        return super.quit()
    }

    private fun handleRequest(target: T) {
        val url = mRequestMap[target] ?: return
        val bitmap = mFlickrFetchr.fetchPhoto(url) ?: return
        responseHandler.post {
            val theUrl = mRequestMap[target]
            if (Objects.requireNonNull(theUrl) != url || mHasQuit) {
                return@post
            }
            mRequestMap.remove(target)
            onThumbnailDownloaded(target, bitmap)
        }
    }
}
