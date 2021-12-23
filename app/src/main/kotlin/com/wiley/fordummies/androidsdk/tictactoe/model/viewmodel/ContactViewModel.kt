package com.wiley.fordummies.androidsdk.tictactoe.model.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.wiley.fordummies.androidsdk.tictactoe.model.ContactLiveData

/**
 * View model class for Contacts, displayed as Strings.
 *
 * Source: https://medium.com/androiddevelopers/lifecycle-aware-data-loading-with-android-architecture-components-f95484159de4
 *
 * Created by acc on 2021/08/03.
 */
class ContactViewModel(application: Application?) :
	AndroidViewModel(application!!) {
	val allContacts: ContactLiveData = ContactLiveData(application!!)

}
