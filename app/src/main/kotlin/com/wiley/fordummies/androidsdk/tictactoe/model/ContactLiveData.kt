package com.wiley.fordummies.androidsdk.tictactoe.model

import android.content.Context
import androidx.lifecycle.MutableLiveData

class ContactLiveData(private val mContext: Context) :
	MutableLiveData<List<Contact?>?>() {
	private val TAG = javaClass.simpleName
}
