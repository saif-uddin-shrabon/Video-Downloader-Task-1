package com.cmed.myapplication.Viewmodel

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.view.View
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.ViewModel
import com.cmed.myapplication.R
import com.cmed.myapplication.View.Download
import com.cmed.myapplication.View.MainActivity


class DownloadViewModel : ViewModel() {


    private val CHANNEL_ID = "Download_Channel"

    // declaring variables
    lateinit var notificationManager: NotificationManager
    lateinit var notificationChannel: NotificationChannel
    private val NOTIFICATION_ID = 1





     fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelName = "Download Channel"
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel(CHANNEL_ID, channelName, importance)
            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }
    fun updateNotification(context: Context, progress: Int){

        notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationSoundUri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)




        val intent = Intent(context, Download::class.java)
        val pendingIntent = PendingIntent.getActivity(context, 0, intent,
            PendingIntent.FLAG_IMMUTABLE)

        val customNotification =
            NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.notification_icon)
                .setStyle(NotificationCompat.DecoratedCustomViewStyle())
                .setContentTitle("Downloading..")
                .setContentIntent(pendingIntent)
                .setProgress(100, progress, false)
                .setOngoing(false)
                .setSound(notificationSoundUri)
                .build()



        notificationManager.notify(NOTIFICATION_ID, customNotification)
    }



}