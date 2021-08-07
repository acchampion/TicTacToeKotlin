package com.wiley.fordummies.androidsdk.tictactoe.model

import android.content.Context
import android.provider.ContactsContract
import androidx.lifecycle.LiveData
import com.wiley.fordummies.androidsdk.tictactoe.R

/**
 * Represents observable list of contacts, expressed as a list of strings.
 *
 * Source: https://medium.com/androiddevelopers/lifecycle-aware-data-loading-with-android-architecture-components-f95484159de4
 *
 * Created by acc on 2021/08/03.
 */
class ContactLiveData(private val mContext: Context) : LiveData<List<String?>?>() {
    companion object {
        private val PROJECTION = arrayOf(
            ContactsContract.Contacts._ID,
            ContactsContract.Contacts.LOOKUP_KEY,
            ContactsContract.Contacts.DISPLAY_NAME_PRIMARY
        )

        /*
	 * Defines an array that contains column names to move from
	 * the Cursor to the ListView.
	 */
        private val FROM_COLUMNS = arrayOf(
            ContactsContract.Contacts.DISPLAY_NAME_PRIMARY
        )

        /*
	 * Defines an array that contains resource ids for the layout views
	 * that get the Cursor column contents. The id is pre-defined in
	 * the Android framework, so it is prefaced with "android.R.id"
	 */
        private val TO_IDS = intArrayOf(
            R.id.contact_info
        )
    }
}
