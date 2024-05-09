package com.wiley.fordummies.androidsdk.tictactoe.network

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.annotation.Keep
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.wiley.fordummies.androidsdk.tictactoe.R
import com.wiley.fordummies.androidsdk.tictactoe.TicTacToeApplication
import com.wiley.fordummies.androidsdk.tictactoe.model.Settings
import com.wiley.fordummies.androidsdk.tictactoe.model.SettingsDataStore
import com.wiley.fordummies.androidsdk.tictactoe.ui.activity.PhotoGalleryActivity.Companion.newIntent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

@Keep
class PollWorker(private val mContext: Context, workerParams: WorkerParameters) : CoroutineWorker(
	mContext, workerParams
) {
	private val mDataStore = TicTacToeApplication.getDataStore()
	private val TAG = javaClass.simpleName
	override suspend fun doWork(): Result {
		withContext(Dispatchers.IO) {
			Timber.tag(TAG).i("Work request triggered")

			val query = mDataStore.getString(Settings.Keys.PREF_SEARCH_QUERY, "")
			val lastResultId = mDataStore.getString(Settings.Keys.PREF_LAST_RESULT_ID, "")

			val itemList = if (query.isEmpty()) {
				FlickrFetchr().fetchPhotosRequest()
					.execute()
					.body()
					?.photos
					?.galleryItems
			} else {
				FlickrFetchr().searchPhotosRequest(query)
					.execute()
					.body()
					?.photos
					?.galleryItems
			} ?: emptyList()


			if (itemList.isEmpty()) {
				return@withContext Result.success()
			}

			val resultId = itemList.first().id
			if (resultId == lastResultId) {
				Timber.tag(TAG).i("Got an old result: %s", resultId)
			} else {
				Timber.tag(TAG).i("Got a new result: %s", resultId)
				mDataStore.putString(Settings.Keys.PREF_LAST_RESULT_ID, resultId)
				val intent = newIntent(mContext)
				val pendingIntent = PendingIntent.getActivity(
					mContext, 0,
					intent, PendingIntent.FLAG_IMMUTABLE
				)
				val resources = mContext.resources
				val notification: Notification =
					NotificationCompat.Builder(mContext, NOTIFICATION)
						.setTicker(resources.getString(R.string.new_pictures_title))
						.setSmallIcon(android.R.drawable.ic_menu_report_image)
						.setContentTitle(resources.getString(R.string.new_pictures_title))
						.setContentText(resources.getString(R.string.new_pictures_text))
						.setContentIntent(pendingIntent)
						.setAutoCancel(true)
						.build()

				/*NotificationManagerCompat notificationManager = NotificationManagerCompat.from(mContext);
		notificationManager.notify(0, notification);

		mContext.sendBroadcast(new Intent(ACTION_SHOW_NOTIFICATION), PERM_PRIVATE);*/
				showBackgroundNotification(
					0,
					notification
				)
			}
		}
		return Result.success()
	}

	private fun showBackgroundNotification(requestCode: Int, notification: Notification) {
		val intent = Intent(ACTION_SHOW_NOTIFICATION)
		intent.putExtra(REQUEST_CODE, requestCode)
		intent.putExtra(NOTIFICATION, notification)
		mContext.sendOrderedBroadcast(intent, PERM_PRIVATE)
	}

	companion object {
		const val ACTION_SHOW_NOTIFICATION =
			"com.wiley.fordummies.androidsdk.tictactoe.SHOW_NOTIFICATION"
		const val PERM_PRIVATE = "com.wiley.fordummies.androidsdk.tictactoe.PRIVATE"
		const val REQUEST_CODE = "REQUEST_CODE"
		const val NOTIFICATION = "NOTIFICATION"
	}
}
