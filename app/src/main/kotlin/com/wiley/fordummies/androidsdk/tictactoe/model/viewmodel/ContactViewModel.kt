package com.wiley.fordummies.androidsdk.tictactoe.model.viewmodel

import android.app.Application
import android.content.ContentResolver
import android.database.Cursor
import android.provider.ContactsContract
import androidx.annotation.Keep
import androidx.lifecycle.AndroidViewModel
import com.wiley.fordummies.androidsdk.tictactoe.model.Contact
import com.wiley.fordummies.androidsdk.tictactoe.model.ContactLiveData
import com.wiley.fordummies.androidsdk.tictactoe.model.ContactRepository
import timber.log.Timber
import java.util.concurrent.CopyOnWriteArrayList

/**
 * View model class for Contacts, displayed as Strings.
 *
 * Source: https://medium.com/androiddevelopers/lifecycle-aware-data-loading-with-android-architecture-components-f95484159de4
 *
 * Created by acc on 2021/08/03.
 */
@Keep
class ContactViewModel(application: Application) : AndroidViewModel(application) {
	private var mRepository: ContactRepository
	private var mAllContactsData: ContactLiveData
	lateinit var mAllContactsList: List<Contact>
	var mContactList: MutableList<Contact> = CopyOnWriteArrayList()

	private val classTag = javaClass.simpleName

	private val PROJECTION = arrayOf(
		ContactsContract.Contacts._ID,
		ContactsContract.Contacts.LOOKUP_KEY,
		ContactsContract.Contacts.DISPLAY_NAME_PRIMARY
	)

	init {
		mRepository = ContactRepository(application)
		mAllContactsData = ContactLiveData(application)
	}

	fun getContactsLiveData(resolver: ContentResolver): ContactLiveData {
		mContactList = loadContacts(resolver)
		mAllContactsData.value = mContactList
		assert(mAllContactsData.value != null)
		return mAllContactsData
	}

	fun loadContacts(mResolver: ContentResolver): MutableList<Contact> {
		val cursor: Cursor? = mResolver.query(
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
					if (cursor.columnCount > 1) {
						val nameIndex =
							cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)
						if (nameIndex >= 0) {
							val contactName = cursor.getString(nameIndex)
							val contact = Contact(contactName)
							mContactList.add(contact)
							cursor.moveToNext()
							position = cursor.position
						} else {
							Timber.tag(classTag).e("Invalid column index")
						}
					}
				}
			}
		} finally {
			if (cursor != null && cursor.count > 0) {
				cursor.close()
			}
		}

		return mContactList
	}

	fun getAllContacts(): ContactLiveData {
		mAllContactsList = mRepository.mContactList
		mAllContactsData.value = mAllContactsList
		assert(mAllContactsData.value != null)
		return mAllContactsData
	}
}
