package com.wiley.fordummies.androidsdk.tictactoe.model

import android.database.Cursor
import android.database.CursorWrapper

import com.wiley.fordummies.androidsdk.tictactoe.model.AccountDbSchema.AccountsTable

/**
 * Class that wraps around a Cursor for an Account model object.
 *
 * Created by adamcchampion on 2017/08/04.
 */

class AccountCursorWrapper(cursor: Cursor) : CursorWrapper(cursor) {

    val account: Account
        get() {
            val name = getString(getColumnIndex(AccountsTable.Cols.NAME))
            val password = getString(getColumnIndex(AccountsTable.Cols.PASSWORD))

            return Account(name, password)
        }
}
