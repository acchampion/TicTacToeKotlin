package com.wiley.fordummies.androidsdk.tictactoe.ui

import androidx.fragment.app.Fragment
import com.wiley.fordummies.androidsdk.tictactoe.R

/**
 * Activity for user login.
 *
 * Created by adamcchampion on 2017/08/03.
 */

class LoginActivity : SingleFragmentActivity() {
    override fun createFragment(): Fragment {
        return LoginFragment()
    }
    override fun getLayoutResId(): Int {
        return R.layout.activity_masterdetail
    }
}
