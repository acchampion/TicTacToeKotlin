package com.wiley.fordummies.androidsdk.tictactoe.model

import android.app.Application
import android.content.Context
import com.wiley.fordummies.androidsdk.tictactoe.concurrency.ExecutorRunner
import java.util.concurrent.CopyOnWriteArrayList

class ContactRepository(application: Application) {
    private val mContext: Context
    private val mRunner: ExecutorRunner
    var mContactList: MutableList<Contact> = CopyOnWriteArrayList()

    init {
        mContext = application
        mRunner = ExecutorRunner()
    }
}
