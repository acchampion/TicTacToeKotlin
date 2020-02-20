package com.wiley.fordummies.androidsdk.tictactoe

import android.app.Application
import timber.log.Timber
import timber.log.Timber.DebugTree

class TicTacToeApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
        }
    }
}