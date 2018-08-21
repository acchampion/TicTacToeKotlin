package com.wiley.fordummies.androidsdk.tictactoe

import android.support.v4.app.Fragment

/**
 * Activity for managing user accounts.
 *
 * Created by adamcchampion on 2017/08/05.
 */

class AccountActivity : SingleFragmentActivity() {
    override fun createFragment(): Fragment {
        return AccountFragment()
    }

    override fun getLayoutResId(): Int {
        return R.layout.activity_masterdetail
    }
}
