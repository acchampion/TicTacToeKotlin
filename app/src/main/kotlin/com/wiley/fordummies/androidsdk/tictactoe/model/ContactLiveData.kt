package com.wiley.fordummies.androidsdk.tictactoe.model

import android.content.Context
import androidx.annotation.Keep
import androidx.lifecycle.MutableLiveData

@Keep
class ContactLiveData(private val mContext: Context) :
	MutableLiveData<List<Contact?>?>() {
	private val classTag = javaClass.simpleName
}
