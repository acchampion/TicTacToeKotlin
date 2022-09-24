package com.wiley.fordummies.androidsdk.tictactoe.model

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.wiley.fordummies.androidsdk.tictactoe.concurrency.ExecutorRunner

class ContactLiveData(private val mContext: Context) :
	MutableLiveData<List<Contact?>?>() {
	private val mRunner = ExecutorRunner()
	private val TAG = javaClass.simpleName
}
