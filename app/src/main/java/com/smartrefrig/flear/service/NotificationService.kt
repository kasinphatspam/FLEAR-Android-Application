package com.smartrefrig.flear.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.annotation.RequiresApi
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.smartrefrig.flear.AlertSleepyActivity
import com.smartrefrig.flear.MainActivity
import com.smartrefrig.flear.R
import java.text.SimpleDateFormat
import java.util.*

class NotificationService : FirebaseMessagingService() {

    private lateinit var context : Context

    @RequiresApi(api = Build.VERSION_CODES.N)
    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        super.onMessageReceived(remoteMessage)
        context = applicationContext

        if(remoteMessage!!.notification!!.title == "Drowsiness Alert!!"){
            val intent = Intent(context,AlertSleepyActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
        }else{
            showNotification(remoteMessage.notification)
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private fun showNotification(notification: RemoteMessage.Notification?) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notificationId = createID()
        val channelId = "channel-id"
        val channelName = "Channel Name"
        val importance = NotificationManager.IMPORTANCE_HIGH

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val mChannel = NotificationChannel(
                channelId, channelName, importance
            )
            notificationManager.createNotificationChannel(mChannel)
        }

        val mBuilder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)//R.mipmap.ic_launcher
            .setContentTitle(notification!!.title)
            .setContentText(notification.body)
            .setVibrate(longArrayOf(100, 250))
            .setLights(Color.YELLOW, 500, 5000)
            .setAutoCancel(true)
            .setColor(ContextCompat.getColor(context, R.color.colorPrimary))

        val stackBuilder = TaskStackBuilder.create(context)
        stackBuilder.addNextIntent(Intent(context, MainActivity::class.java))
        val resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
        mBuilder.setContentIntent(resultPendingIntent)

        notificationManager.notify(notificationId, mBuilder.build())

    }

    fun createID(): Int {
        val now = Date()
        return Integer.parseInt(SimpleDateFormat("ddHHmmss", Locale.FRENCH).format(now))
    }

    override fun onNewToken(s: String?) {
        super.onNewToken(s)
    }
}