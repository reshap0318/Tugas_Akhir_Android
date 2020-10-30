package com.minangdev.myta.Helper

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.minangdev.myta.R

class FirebaseNotificationService : FirebaseMessagingService()  {

    override fun onMessageReceived(remote: RemoteMessage) {
        super.onMessageReceived(remote)
        if(remote?.notification != null){
            showNotification(remote)
        }
    }

    private fun showNotification(remote: RemoteMessage) {
        val CHANNEL_ID = "Channel_1"
        val CHANNEL_NAME = "Firebase Chanel"
        val NOTIFICATION_ID = 1

        val title = remote.notification?.title
        val message = remote.notification?.body

        val mNotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val mBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            /* Create or update. */
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT)
            mBuilder.setChannelId(CHANNEL_ID)
            mNotificationManager.createNotificationChannel(channel)
        }

        val notification = mBuilder.build()
        mNotificationManager.notify(NOTIFICATION_ID, notification)
    }

}