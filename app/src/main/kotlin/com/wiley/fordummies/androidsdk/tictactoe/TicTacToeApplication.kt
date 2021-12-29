package com.wiley.fordummies.androidsdk.tictactoe

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.util.Log
import androidx.work.Configuration
import timber.log.Timber
import timber.log.Timber.DebugTree

const val NOTIFICATION_CHANNEL_ID = "flickr_poll"

class TicTacToeApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
        }

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			val name = getString(R.string.notification_channel_name)
			val importance = NotificationManager.IMPORTANCE_DEFAULT
			val channel =
				NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance)
			val notificationManager = getSystemService(
				NotificationManager::class.java
			)
			notificationManager.createNotificationChannel(channel)
		}
    }

	fun getWorkManagerConfiguration(): Configuration {
		return Configuration.Builder()
			.setMinimumLoggingLevel(Log.INFO)
			.build()
	}
}
