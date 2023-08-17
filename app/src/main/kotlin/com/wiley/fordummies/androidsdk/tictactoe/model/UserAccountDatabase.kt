package com.wiley.fordummies.androidsdk.tictactoe.model

import android.content.Context
import androidx.annotation.Keep
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * Database class for UserAccount processing with Room.
 *
 * Source: https://developer.android.com/codelabs/android-room-with-a-view
 *
 * Created by acc on 2021/08/04.
 */
@Keep
@Database(entities = [UserAccount::class], version = 1, exportSchema = false)
abstract class UserAccountDatabase : RoomDatabase() {
    abstract val userAccountDao: UserAccountDao

    companion object {
        @Volatile
        private var sInstance: UserAccountDatabase? = null
        private const val sNumberOfThreads = 2
        val databaseWriteExecutor: ExecutorService = Executors.newFixedThreadPool(sNumberOfThreads)
        fun getDatabase(context: Context): UserAccountDatabase {
            if (sInstance == null) {
                synchronized(UserAccountDatabase::class.java) {
                    if (sInstance == null) {
                        sInstance = Room.databaseBuilder(
                            context.applicationContext,
                            UserAccountDatabase::class.java, "useraccount_database"
                        )
                            .build()
                    }
                }
            }
            return sInstance!!
        }
    }
}
