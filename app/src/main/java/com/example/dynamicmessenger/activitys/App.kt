package com.example.dynamicmessenger.activitys

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.util.Log
import com.example.dynamicmessenger.common.SharedConfigs

class App: Application() {
    override fun onCreate() {
        super.onCreate()
        SharedConfigs.init(this)
    }

//    override fun attachBaseContext(base: Context?) {
//        val lang = SharedConfigs.appLang.value ?: Locale.getDefault().getLanguage()
//        super.attachBaseContext(LocalizationUtil.applyLanguage(base!!, lang))
//    }
}
