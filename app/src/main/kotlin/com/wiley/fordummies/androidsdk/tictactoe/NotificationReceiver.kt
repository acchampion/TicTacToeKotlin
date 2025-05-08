package com.wiley.fordummies.androidsdk.tictactoe

import android.Manifest
import android.app.Activity
import android.app.Notification
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import com.wiley.fordummies.androidsdk.tictactoe.network.PollWorker
import timber.log.Timber

class NotificationReceiver : BroadcastReceiver() {
    private val classTag = javaClass.simpleName
    val requestCode = 1
    override fun onReceive(context: Context, intent: Intent) {
        val resultCode = resultCode
        Timber.tag(classTag).i("Received result: %d", resultCode)
        if (resultCode != Activity.RESULT_OK) {
            // A foreground activity canceled the broadcast
            return
        }
        val requestCode = intent.getIntExtra(PollWorker.REQUEST_CODE, 0)
        val notification = intent.getParcelableExtra<Notification>(PollWorker.NOTIFICATION)
        val notificationManager = NotificationManagerCompat.from(context)
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ActivityCompat.requestPermissions(
                    context as Activity,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    requestCode
                )
            }
            return
        }
        notificationManager.notify(requestCode, notification!!)
    }
}
