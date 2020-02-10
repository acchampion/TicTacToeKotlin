package com.wiley.fordummies.androidsdk.tictactoe.ui

import androidx.fragment.app.Fragment

/**
 * Activity that shows users' contacts.
 *
 * Created by adamcchampion on 2017/08/16.
 */

class ContactsActivity : SingleFragmentActivity() {
    override fun createFragment(): Fragment {
        return ContactsFragment()
    }
}
