package com.wiley.fordummies.androidsdk.tictactoe.ui.activity

import androidx.fragment.app.Fragment
import com.wiley.fordummies.androidsdk.tictactoe.ui.fragment.ImagesFragment
import com.wiley.fordummies.androidsdk.tictactoe.ui.SingleFragmentActivity

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
