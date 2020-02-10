package com.wiley.fordummies.androidsdk.tictactoe.ui

import androidx.fragment.app.Fragment

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
