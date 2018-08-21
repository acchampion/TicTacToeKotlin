package com.wiley.fordummies.androidsdk.tictactoe

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.IBinder
import android.util.Log

@SuppressWarnings("LogNotTimber")
class MediaPlaybackService : Service() {
    internal lateinit var player: MediaPlayer

    private val TAG = javaClass.simpleName

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
            val audioFileURIString = extras.getString("URIString")
            val audioFileURI = Uri.parse(audioFileURIString)
            Log.d(TAG, "URI = " + audioFileURI.toString())
            try {
                player.reset()
                player.setDataSource(this.applicationContext, audioFileURI)
                player.prepare()
                player.start()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        return Service.START_STICKY
    }

    override fun onDestroy() {
        player.stop()
    }
}
