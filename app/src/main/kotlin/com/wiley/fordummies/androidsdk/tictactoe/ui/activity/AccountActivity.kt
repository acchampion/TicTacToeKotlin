package com.wiley.fordummies.androidsdk.tictactoe.ui.activity

import androidx.annotation.Keep
import androidx.fragment.app.Fragment
import com.wiley.fordummies.androidsdk.tictactoe.R
import com.wiley.fordummies.androidsdk.tictactoe.ui.SingleFragmentActivity
import com.wiley.fordummies.androidsdk.tictactoe.ui.fragment.AccountFragment

/**
 * Activity for managing user accounts.
 *
 * Created by adamcchampion on 2017/08/05.
 */
@Keep
class AccountActivity : SingleFragmentActivity() {
    override fun createFragment(): Fragment {
        return AccountFragment()
    }

    override fun getLayoutResId(): Int {
        return R.layout.activity_masterdetail
    }
}
