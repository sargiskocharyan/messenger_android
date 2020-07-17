package com.example.dynamicmessenger.utils

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.graphics.Color
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.getSystemService
import com.example.dynamicmessenger.R


class NotificationMessages {
    companion object {
        fun setNotificationMessage(messageTitle: String, message: String, context: Context, manager: NotificationManager) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                setupChannels(manager)
            }

            val builder = NotificationCompat.Builder(context, "101")
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle(messageTitle)
                .setContentText(message)
    //            .setContentIntent(pendingIntent)
                .build()
            manager.notify(6578, builder)
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        fun setupChannels(notificationManager: NotificationManager?) {
            val adminChannelName: CharSequence = "New notification"
            val adminChannelDescription = "Device to device notification"
            val adminChannel: NotificationChannel
            adminChannel = NotificationChannel(
                "101",
                adminChannelName,
                NotificationManager.IMPORTANCE_HIGH
            )
            adminChannel.description = adminChannelDescription
            adminChannel.enableLights(true)
            adminChannel.lightColor = Color.RED
            adminChannel.enableVibration(true)
            notificationManager?.createNotificationChannel(adminChannel)
        }
    }
}