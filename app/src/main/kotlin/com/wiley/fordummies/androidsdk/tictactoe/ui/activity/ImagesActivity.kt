package com.wiley.fordummies.androidsdk.tictactoe.ui.activity

import androidx.annotation.Keep
import androidx.fragment.app.Fragment
import com.wiley.fordummies.androidsdk.tictactoe.ui.SingleFragmentActivity
import com.wiley.fordummies.androidsdk.tictactoe.ui.fragment.ImagesFragment

/**
 * Activity for showing and hosting images.
 *
 * Created by adamcchampion on 2017/08/12.
 */
@Keep
class ImagesActivity : SingleFragmentActivity() {
    override fun createFragment(): Fragment {
        return ImagesFragment()
    }
}
