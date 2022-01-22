package com.wiley.fordummies.androidsdk.tictactoe.model

import android.annotation.SuppressLint
import android.content.Context
import android.os.AsyncTask
import android.provider.ContactsContract
import androidx.lifecycle.LiveData
import timber.log.Timber
import java.util.*

/**
 * LiveData class that encapsulates the results of fetching contacts' names.
 *
 *
 *
 * Created by acc on 2021/08/09.
 */
class ContactLiveData(private val mContext: Context) :
	LiveData<List<Contact>>() {

	private val TAG = javaClass.simpleName

	@SuppressLint("StaticFieldLeak, Deprecated")
	private fun loadContacts() {

		object : AsyncTask<Void?, Void?, List<Contact>>() {

			override fun onPostExecute(contactList: List<Contact>) {
				value = contactList
			}

			override fun doInBackground(vararg params: Void?): List<Contact> {
				val contactList: MutableList<Contact> = ArrayList()
				val resolver = mContext.contentResolver
				val cursor = resolver.query(
					ContactsContract.Contacts.CONTENT_URI,
					PROJECTION,
					null,
					null,
					ContactsContract.Contacts.DISPLAY_NAME + " COLLATE LOCALIZED ASC"
				)
				try {
					if (cursor != null) {
						cursor.moveToFirst()
						val count = cursor.count
						var position = cursor.position
						while (position < count) {
							val nameIndex =
								cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)
							if (nameIndex >= 0) {
								val contactName = cursor.getString(nameIndex)
								val contact = Contact(contactName)
								contactList.add(contact)
								cursor.moveToNext()
								position = cursor.position
							} else {
								Timber.tag(TAG).e("Invalid column index")
							}
						}
					}
				} finally {
					if (cursor != null && cursor.count > 0) {
						cursor.close()
					}
				}
				return contactList
			}
		}.execute()
	}

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
		/*private val FROM_COLUMNS = arrayOf(
			ContactsContract.Contacts.DISPLAY_NAME_PRIMARY
		)*/
	}

	init {
		loadContacts()
	}
}
