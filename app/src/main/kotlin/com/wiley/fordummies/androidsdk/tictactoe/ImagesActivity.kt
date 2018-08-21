package com.wiley.fordummies.androidsdk.tictactoe

import android.support.v4.app.Fragment

/**
 * Activity for showing and hosting images.
 *
 * Created by adamcchampion on 2017/08/12.
 */

class ImagesActivity : SingleFragmentActivity() {
    override fun createFragment(): Fragment {
        return ImagesFragment()
    }
}
