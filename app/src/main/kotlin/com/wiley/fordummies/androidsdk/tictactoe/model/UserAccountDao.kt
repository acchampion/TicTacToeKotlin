package com.wiley.fordummies.androidsdk.tictactoe.model

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface UserAccountDao {
    @get:Query("SELECT rowid, name, password FROM useraccount")
    val allUserAccounts: LiveData<List<UserAccount>>

    @Query("SELECT rowid, name, password FROM useraccount WHERE name LIKE :name AND password LIKE :password LIMIT 1")
    fun findByName(name: String, password: String): LiveData<UserAccount>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(userAccount: UserAccount)

    @Update
    fun updateUserAccount(userAccount: UserAccount)

    @Delete
    fun delete(userAccount: UserAccount)
}
