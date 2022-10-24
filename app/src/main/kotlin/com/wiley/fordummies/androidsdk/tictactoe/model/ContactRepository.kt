package com.wiley.fordummies.androidsdk.tictactoe.model

import android.app.Application
import android.content.Context
import java.util.concurrent.CopyOnWriteArrayList

class ContactRepository(application: Application) {
    private val mContext: Context
    var mContactList: MutableList<Contact> = CopyOnWriteArrayList()

    init {
        mContext = application
    }
}
