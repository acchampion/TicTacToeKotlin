package com.wiley.fordummies.androidsdk.tictactoe

import android.os.Bundle
import android.support.v4.app.Fragment

class SplashScreenActivity : SingleFragmentActivity() {
    override fun createFragment(): Fragment {
        return SplashScreenFragment()
    }

    /**
     * Called when the activity is first created.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fragment)
    }

    override fun onStart() {
        super.onStart()

    }
}
