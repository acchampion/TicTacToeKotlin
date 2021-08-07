package com.wiley.fordummies.androidsdk.tictactoe.model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData

/**
 * View model class for Contacts, displayed as Strings.
 *
 * Source: https://medium.com/androiddevelopers/lifecycle-aware-data-loading-with-android-architecture-components-f95484159de4
 *
 * Created by acc on 2021/08/03.
 */
class ContactViewModel(application: Application) : AndroidViewModel(
	application
) {
	private val mListData = MutableLiveData<List<String>>()
	private fun loadData() {
		// TODO: fill in later
	}
}
