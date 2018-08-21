package com.wiley.fordummies.androidsdk.tictactoe

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

import com.wiley.fordummies.androidsdk.tictactoe.AccountDbSchema.AccountsTable

/**
 * Account database helper class.
 *
 * TODO: Convert to Kotlin
 *
 * Created by adamcchampion on 2017/08/04.
 */
@SuppressWarnings("LogNotTimber")
class AccountDbHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(sqLiteDatabase: SQLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE " + AccountsTable.NAME + "(" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                AccountsTable.Cols.NAME + " TEXT, " +
                AccountsTable.Cols.PASSWORD + " TEXT" +
                ")")
    }

    override fun onUpgrade(database: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        Log.w("Example", "Example: upgrading database; dropping and recreating tables.")
        database.execSQL("DROP TABLE IF EXISTS " + AccountsTable.NAME)
        onCreate(database)
    }

    companion object {
        private const val DATABASE_NAME = "TicTacToe.db"
        private const val DATABASE_VERSION = 1
    }
}
