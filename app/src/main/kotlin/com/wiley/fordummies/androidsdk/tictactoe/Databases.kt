package com.wiley.fordummies.androidsdk.tictactoe

import android.database.sqlite.SQLiteDatabase

/**
 * Utility class for database transactions via Kotlin.
 *
 * Source: Jake Wharton, http://jakewharton.com/kotlin-is-here/
 *
 * Created by adamcchampion on 2017/08/31.
 */
inline fun SQLiteDatabase.transaction(body: (SQLiteDatabase) -> Unit) {
    beginTransaction()
    try {
        body(this)
        setTransactionSuccessful()
    } finally {
        endTransaction()
    }
}
