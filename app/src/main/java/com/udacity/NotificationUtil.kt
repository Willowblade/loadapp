package com.udacity

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Parcelable
import androidx.core.app.NotificationCompat
import kotlinx.android.parcel.Parcelize

private val NOTIFICATION_ID = 0
private val REQUEST_CODE = 0
private val FLAGS = 0

@Parcelize
data class DownloadDetails(val filename: String, val success: Boolean) : Parcelable

fun NotificationManager.sendDownloadedNotification(messageBody: String, success: Boolean, applicationContext: Context) {
    val contentIntent = Intent(applicationContext, MainActivity::class.java)

    val contentPendingIntent = PendingIntent.getActivity(applicationContext, NOTIFICATION_ID, contentIntent, PendingIntent.FLAG_UPDATE_CURRENT)

    val goToDetailsIntent = Intent(applicationContext, DetailActivity::class.java)
    goToDetailsIntent.putExtra("details", DownloadDetails(messageBody, success))

    val goToDetailsPendingIntent = PendingIntent.getActivity(applicationContext, REQUEST_CODE, goToDetailsIntent, PendingIntent.FLAG_UPDATE_CURRENT)

    val builder = NotificationCompat.Builder(applicationContext, applicationContext.getString(R.string.download_channel_id))
            .setContentTitle(if (success) "Download complete!" else "Download failed!")
            .setSmallIcon(R.drawable.ic_assistant_black_24dp)
            .setContentText("Your download of $messageBody is done")
            .setContentIntent(contentPendingIntent)
            .setAutoCancel(true)
            .addAction(
                    R.drawable.ic_assistant_black_24dp,
                    "See more details",
                    goToDetailsPendingIntent
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

    notify(NOTIFICATION_ID, builder.build())
}
