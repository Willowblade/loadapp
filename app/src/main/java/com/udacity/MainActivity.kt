package com.udacity

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity() {

    private var isRunning: Boolean = false;
    private var downloadID: Long = 0

    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action
    private lateinit var downloadManager: DownloadManager

    private var downloading: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        createChannel(getString(R.string.download_channel_id), "Download")
        custom_button.setOnClickListener {
            if (downloading == null) {
                if (!isRadioSelected()) {
                    val toast = Toast.makeText(applicationContext, "Please select an option before pressing download", Toast.LENGTH_SHORT)
                    toast.show()
                    return@setOnClickListener
                } else {
                    download()
                }
            }
        }

        custom_button.setCompletedListener {
            downloadAnimationFinished()
        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            println("Received intent $intent")
            if (intent?.action == DownloadManager.ACTION_DOWNLOAD_COMPLETE) {
                val query = DownloadManager.Query()
                query.setFilterByStatus(DownloadManager.STATUS_FAILED or DownloadManager.STATUS_PAUSED or DownloadManager.STATUS_SUCCESSFUL or
                        DownloadManager.STATUS_RUNNING or DownloadManager.STATUS_PENDING)
                val cursor = downloadManager.query(query)
                println("Cursor $cursor")
                var success = false;
                if (cursor != null) {
                    cursor.moveToFirst()
                    val statusIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)
                    val status = cursor.getInt(statusIndex.toInt())

                    if (status == DownloadManager.STATUS_SUCCESSFUL) {
                        println("Successful download!")
                        success = true;
                    } else if (status == DownloadManager.STATUS_FAILED) {
                        println("Failed download!")
                    }
                }
                println("Downloaded! $downloading")
                custom_button.setFinished()
                val notificationManager = ContextCompat.getSystemService(applicationContext, NotificationManager::class.java) as NotificationManager
                var downloadName: String = getDownloadName()
                if (downloading != null) {
                    downloadName = downloading!!
                }
                notificationManager.sendDownloadedNotification(downloadName, success, applicationContext)
                if (isRunning) {
                    val toast = Toast.makeText(applicationContext, "Your download of $downloadName is finished!", Toast.LENGTH_SHORT)
                    toast.show()
                }
                downloading = null
            }
        }
    }

    private fun downloadAnimationFinished() {
        downloading?.let {
            custom_button.resetLoading()
        }
    }

    private fun download() {
        val request =
            DownloadManager.Request(Uri.parse(getUrl()))
                .setTitle(getString(R.string.app_name))
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

        downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID =
            downloadManager.enqueue(request)// enqueue puts the download request in the queue.
        downloading = getDownloadName()
        custom_button.setLoading()
    }

    private fun isRadioSelected(): Boolean {
        return when(download_options.checkedRadioButtonId) {
            glide.id -> true
            project.id -> true
            retrofit.id -> true
            else -> false
        }
    }

    private fun getUrl(): String {
        return when(download_options.checkedRadioButtonId) {
            glide.id -> GLIDE_URL
            project.id -> PROJECT_URL
            retrofit.id -> RETROFIT_URL
            else -> GLIDE_URL
        }
    }

    private fun getDownloadName(): String {
        return when(download_options.checkedRadioButtonId) {
            glide.id -> "Glide"
            project.id -> "Android AppLoader starter"
            retrofit.id -> "Retrofit"
            else -> "Glide"
        }
    }

    private fun createChannel(channelId: String, channelName: String) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                    channelId,
                    channelName,
                    NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.BLUE
            notificationChannel.enableVibration(true)
            notificationChannel.description = "Your download is ready!"

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(notificationChannel)
        }
        // TODO: Step 1.6 END create a channel

    }

    override fun onResume() {
        super.onResume()
        isRunning = true
    }

    override fun onPause() {
        super.onPause()
        isRunning = false
    }


    companion object {
        private const val GLIDE_URL = "https://github.com/bumptech/glide/archive/master.zip"
        private const val PROJECT_URL = "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val RETROFIT_URL = "https://github.com/square/retrofit/archive/master.zip"
        private const val CHANNEL_ID = "channelId"
    }

}
