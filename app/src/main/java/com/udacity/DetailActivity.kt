package com.udacity

import android.app.DownloadManager
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.content_detail.*
import kotlinx.android.synthetic.main.content_main.*
import java.util.logging.Logger

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)


        println("In details view!")
        println(intent)
        if (intent.extras != null && intent.hasExtra("details")) {
            // couldn't seem to get the !! go away
            val details: DownloadDetails = intent.extras!!.get("details") as DownloadDetails
            filename.text = details.filename
            status.text = if (details.success) "Success" else "Fail"
            status.setTextColor(if (details.success) getColor(R.color.green) else getColor(R.color.red))
        } else {
            val logger = Logger.getLogger("Details View")
            logger.warning("Didn't get details from notification intent")
        }

        val notificationManager = ContextCompat.getSystemService(applicationContext, NotificationManager::class.java) as NotificationManager
        notificationManager.cancelAll()

        setSupportActionBar(toolbar)
    }
}
