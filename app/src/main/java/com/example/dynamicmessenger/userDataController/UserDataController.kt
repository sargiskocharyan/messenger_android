package com.example.dynamicmessenger.userDataController

import android.content.Context
import android.content.SharedPreferences
import android.util.Base64
import com.example.dynamicmessenger.common.SharedConfigs
import com.example.dynamicmessenger.common.SharedPrefConstants
import com.example.dynamicmessenger.network.authorization.models.User
import com.example.dynamicmessenger.userDataController.database.SignedUser
import com.google.gson.Gson
import java.security.MessageDigest
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

object SharedPreferencesManager {
    private fun getSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(SharedPrefConstants.sharedPrefCreate, Context.MODE_PRIVATE)
    }

    fun setUserMailExists(context: Context, exists: Boolean) {
        getSharedPreferences(context)
            .edit()
            .putBoolean(SharedPrefConstants.sharedPrefIsMailExist, exists)
            .apply()
    }

    fun getUserMailExists(context: Context): Boolean {
        return getSharedPreferences(context).getBoolean(SharedPrefConstants.sharedPrefIsMailExist, false)
    }

    fun setUserMail(context: Context, mail: String) {
        getSharedPreferences(context)
            .edit()
            .putString(SharedPrefConstants.sharedPrefMail, mail)
            .apply()
    }

    fun getUserMail(context: Context): String {
        return getSharedPreferences(context).getString(SharedPrefConstants.sharedPrefMail, "")!!
    }

    fun setUserCode(context: Context, code: String) {
        getSharedPreferences(context)
            .edit()
            .putString(SharedPrefConstants.sharedPrefCode, code)
            .apply()
    }

    fun getUserCode(context: Context): String {
        return getSharedPreferences(context).getString(SharedPrefConstants.sharedPrefCode, "")!!
    }

    fun deleteUserAllInformation(context: Context) {
        getSharedPreferences(context).edit().clear().apply()
    }

}

object SaveToken {
    fun decrypt(outputString: String?): String {
        if (outputString == null || outputString == "") return ""
        val key = generateKey(SharedPrefConstants.sharedPrefToken)
        val c = Cipher.getInstance("AES")
        c.init(Cipher.DECRYPT_MODE, key)
        val decodedValue: ByteArray = Base64.decode(outputString, Base64.DEFAULT)
        val decValue: ByteArray = c.doFinal(decodedValue)
        return String(decValue)
    }

    fun encrypt(data: String): String  {
        val key = generateKey(SharedPrefConstants.sharedPrefToken)
        val c = Cipher.getInstance("AES")
        c.init(Cipher.ENCRYPT_MODE,key)
        val encVal:ByteArray = c.doFinal(data.toByteArray())
        return Base64.encodeToString(encVal, Base64.DEFAULT)
    }

    private fun generateKey(password: String): SecretKeySpec  {
        val digest = MessageDigest.getInstance("SHA-256")
        val bytes :ByteArray = password.toByteArray()
        digest.update(bytes, 0, bytes.size)
        val key:ByteArray = digest.digest()
        return SecretKeySpec(key, "AES")
    }
}

