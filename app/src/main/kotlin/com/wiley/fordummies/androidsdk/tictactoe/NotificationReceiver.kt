package com.wiley.fordummies.androidsdk.tictactoe

import android.app.Activity
import android.app.Notification
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationManagerCompat
import com.wiley.fordummies.androidsdk.tictactoe.network.PollWorker
import timber.log.Timber

class NotificationReceiver : BroadcastReceiver() {
    private val TAG = javaClass.simpleName
    override fun onReceive(context: Context, intent: Intent) {
        val resultCode = resultCode
        Timber.tag(TAG).i("Received result: %d", resultCode)
        if (resultCode != Activity.RESULT_OK) {
            // A foreground activity canceled the broadcast
            return
        }
        val requestCode = intent.getIntExtra(PollWorker.REQUEST_CODE, 0)
        val notification = intent.getParcelableExtra<Notification>(PollWorker.NOTIFICATION)
        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.notify(requestCode, notification!!)
    }
}
