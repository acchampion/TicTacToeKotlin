package com.wiley.fordummies.androidsdk.tictactoe.model

import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Fts4
import androidx.room.PrimaryKey
import java.util.Objects

@Fts4
@Keep
@Entity(tableName = "useraccount")
class UserAccount(
    @field:ColumnInfo(name = "name") var name: String,
	@field:ColumnInfo(name = "password") var password: String
) {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "rowid")
    var rowid = 0

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val that = other as UserAccount
        return name == that.name && password == that.password
    }

    override fun hashCode(): Int {
        return Objects.hash(rowid, name, password)
    }

    override fun toString(): String {
        return "UserAccount{" +
                "uid=" + rowid +
                "; name='" + name + '\'' +
                "; password='" + password + '\'' +
                '}'
    }
}
