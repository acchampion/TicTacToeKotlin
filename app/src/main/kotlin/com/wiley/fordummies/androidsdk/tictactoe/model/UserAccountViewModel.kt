package com.wiley.fordummies.androidsdk.tictactoe.model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData

/**
 * ViewModel for the user account storage.
 */
class UserAccountViewModel(application: Application) : AndroidViewModel(application) {
	private val mRepository: UserAccountRepository = UserAccountRepository(application)
	var allUserAccounts: LiveData<List<UserAccount>> = mRepository.allUserAccounts

	fun containsUserAccount(userAccount: UserAccount): Boolean {
		var accountInList = false
		val userAccountLiveData = mRepository.findUserAccountByName(userAccount)
		val theUserAccount = userAccountLiveData.value
		if (theUserAccount?.name == userAccount.name && theUserAccount.password == userAccount.password) {
			accountInList = true
		}
		return accountInList
	}

	fun getUserAccount(userAccount: UserAccount): LiveData<UserAccount> {
		return mRepository.findUserAccountByName(userAccount)
	}

	fun insert(userAccount: UserAccount) {
		mRepository.insert(userAccount)
	}

}
