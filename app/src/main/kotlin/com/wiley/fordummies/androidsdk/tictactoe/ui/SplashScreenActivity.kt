package com.wiley.fordummies.androidsdk.tictactoe.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.wiley.fordummies.androidsdk.tictactoe.R

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
}
