package com.wiley.fordummies.androidsdk.tictactoe.network

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.wiley.fordummies.androidsdk.tictactoe.NOTIFICATION_CHANNEL_ID
import com.wiley.fordummies.androidsdk.tictactoe.R
import com.wiley.fordummies.androidsdk.tictactoe.model.GalleryItem
import com.wiley.fordummies.androidsdk.tictactoe.model.QueryPreferences
import com.wiley.fordummies.androidsdk.tictactoe.ui.activity.PhotoGalleryActivity.Companion.newIntent
import timber.log.Timber

class PollWorker(private val mContext: Context, workerParams: WorkerParameters) : Worker(
    mContext, workerParams
) {
    private val TAG = javaClass.simpleName
    override fun doWork(): Result {
        Timber.tag(TAG).i("Work request triggered")
        val query = QueryPreferences.getStoredQuery(mContext)
        val lastResultId = QueryPreferences.getLastResultId(mContext)

		val itemList: List<GalleryItem> = if (query!!.isEmpty()) {
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
            return Result.success()
        }

        val resultId = itemList.first().id
        if (resultId == lastResultId) {
            Timber.tag(TAG).i("Got an old result: %s", resultId)
        } else {
            Timber.tag(TAG).i("Got a new result: %s", resultId)
            QueryPreferences.setLastResultId(mContext, resultId)
            val intent = newIntent(mContext)
            val pendingIntent = PendingIntent.getActivity(mContext, 0,
                intent, PendingIntent.FLAG_IMMUTABLE
            )
            val resources = mContext.resources
            val notification: Notification =
                NotificationCompat.Builder(mContext, NOTIFICATION_CHANNEL_ID)
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
