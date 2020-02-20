package com.wiley.fordummies.androidsdk.tictactoe.ui

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.wiley.fordummies.androidsdk.tictactoe.model.AccountDbSchema.AccountsTable
import timber.log.Timber

/**
 * Account database helper class.
 *
 * Created by adamcchampion on 2017/08/04.
 */
class AccountDbHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    // private val TAG = AccountDbHelper::class.java.simpleName
    override fun onCreate(sqLiteDatabase: SQLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE " + AccountsTable.NAME + "(" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                AccountsTable.Cols.NAME + " TEXT, " +
                AccountsTable.Cols.PASSWORD + " TEXT" +
                ")")
    }

    override fun onUpgrade(database: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        Timber.w("Example: upgrading database; dropping and recreating tables.")
        database.execSQL("DROP TABLE IF EXISTS " + AccountsTable.NAME)
        onCreate(database)
    }

    companion object {
        private const val DATABASE_NAME = "TicTacToe.db"
        private const val DATABASE_VERSION = 1
    }
}
