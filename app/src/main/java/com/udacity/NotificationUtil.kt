package com.udacity

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat

private val NOTIFICATION_ID = 0
private val REQUEST_CODE = 0
private val FLAGS = 0

fun NotificationManager.sendDownloadedNotification(messageBody: String, applicationContext: Context) {
    val contentIntent = Intent(applicationContext, MainActivity::class.java)

    val contentPendingIntent = PendingIntent.getActivity(applicationContext, NOTIFICATION_ID, contentIntent, PendingIntent.FLAG_UPDATE_CURRENT)

    val goToDetailsIntent = Intent(applicationContext, DetailActivity::class.java)
    val goToDetailsPendingIntent = PendingIntent.getActivity(applicationContext, REQUEST_CODE, goToDetailsIntent, FLAGS)

    val builder = NotificationCompat.Builder(applicationContext, applicationContext.getString(R.string.download_channel_id))
            .setContentTitle("Download complete!")
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentText("Your download of $messageBody is now ready to view")
            .setContentIntent(contentPendingIntent)
            .setAutoCancel(true)
            .addAction(
                    R.drawable.ic_launcher_background,
                    "Details",
                    goToDetailsPendingIntent
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

    notify(NOTIFICATION_ID, builder.build())
}