package com.example.dynamicmessenger.userDataController.database

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.dynamicmessenger.common.AppLangKeys
import com.example.dynamicmessenger.common.SharedConfigs
import com.example.dynamicmessenger.network.authorization.models.University

@Entity(tableName = "signed_user")
data class SignedUser(
//    @ColumnInfo(name = "name")
    @PrimaryKey
    val _id: String,
    val name: String?,
    val lastname: String?,
    val username: String,
    val email: String,
    @Embedded
    val university: University?,
    var avatarURL: String?
)

@Entity(tableName = "user_token")
data class UserToken(
    @PrimaryKey
    val token: String
)
