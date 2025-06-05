package com.wiley.fordummies.androidsdk.tictactoe

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder
import androidx.annotation.Keep
import androidx.core.net.toUri
import timber.log.Timber

@Keep
class MediaPlaybackService : Service() {
	private lateinit var player: MediaPlayer

	override fun onBind(intent: Intent): IBinder? {
		return null
	}

	override fun onCreate() {
		player = MediaPlayer.create(this, R.raw.sample_audio)
		player.apply { isLooping = true }
	}

	override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
		super.onStartCommand(intent, flags, startId)
		val extras = intent.extras
		if (extras != null) {
			val audioFileUriString = extras.getString("UriString")
			if (audioFileUriString != null) {
				val audioFileUri = audioFileUriString.toUri()
				Timber.d("URI = $audioFileUri")
				try {
					player.reset()
					player.setDataSource(this.applicationContext, audioFileUri)
					player.prepare()
					player.start()
				} catch (e: Exception) {
					e.printStackTrace()
				}
			}
		}

		return START_STICKY
	}

	override fun onDestroy() {
		player.stop()
	}
}
