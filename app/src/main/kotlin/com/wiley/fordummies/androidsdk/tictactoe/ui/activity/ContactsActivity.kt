package com.wiley.fordummies.androidsdk.tictactoe.ui.activity

import androidx.annotation.Keep
import androidx.fragment.app.Fragment
import com.wiley.fordummies.androidsdk.tictactoe.ui.SingleFragmentActivity
import com.wiley.fordummies.androidsdk.tictactoe.ui.fragment.ContactsFragment

/**
 * Activity that shows users' contacts.
 *
 * Created by adamcchampion on 2017/08/16.
 */
@Keep
class ContactsActivity : SingleFragmentActivity() {
    override fun createFragment(): Fragment {
        return ContactsFragment()
    }
}
