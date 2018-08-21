package com.wiley.fordummies.androidsdk.tictactoe

import android.support.v4.app.Fragment

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
