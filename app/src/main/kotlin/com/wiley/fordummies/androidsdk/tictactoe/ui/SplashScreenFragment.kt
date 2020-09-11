package com.wiley.fordummies.androidsdk.tictactoe.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.wiley.fordummies.androidsdk.tictactoe.R

/**
 * Fragment for splash screen.
 *
 * Created by adamcchampion on 2017/08/03.
 */

class SplashScreenFragment : Fragment(), View.OnTouchListener {
    private var mIsActive = true
    private val mSplashTime = 500
    private val mTimeIncrement = 100
    private val mSleepTime = 100


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_splash, container, false)
        v.setOnTouchListener(this)
        return v
    }

    override fun onStart() {
        super.onStart()
		val activity = requireActivity()
        // Thread for displaying the SplashScreen
        val splashThread = Thread {
            try {
                var elapsedTime = 0
                while (mIsActive && elapsedTime < mSplashTime) {
                    Thread.sleep(mSleepTime.toLong())
                    if (mIsActive) elapsedTime += mTimeIncrement
                }
            } catch (e: InterruptedException) {
                // do nothing
            } finally {
                activity.finish()
                startActivity(Intent("com.wiley.fordummies.androidsdk.tictactoe.Login"))
            }
        }
        splashThread.start()
    }

    override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {
        if (motionEvent.action == MotionEvent.ACTION_DOWN) {
            mIsActive = false
            view.performClick()
            return true
        }
        return false
    }
}
