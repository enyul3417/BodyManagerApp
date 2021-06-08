package com.example.bodymanagerapp.menu.Settings.Notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.bodymanagerapp.R
import java.text.SimpleDateFormat
import java.util.*
import java.util.logging.SimpleFormatter

class AlarmReceiver : BroadcastReceiver() {
    val CHANNEL_ID = "알람"
    val CHANNEL_NAME = "알람 채널"
    val CHANNEL_DESCRIPTION = "바디 매니저에서 설정한 알람"

    override fun onReceive(context: Context?, intent: Intent?) {
        var week = intent?.getBooleanArrayExtra("weekday")
        var memo = intent?.getStringExtra("memo")

        var calendar = Calendar.getInstance()

        if(week!![calendar.get(Calendar.DAY_OF_WEEK)]) {
            /*var foramt = SimpleDateFormat("HH:mm")
            var time = Date()
            var time1 = foramt.format(time)*/

            // Create an explicit intent for an Activity in your app
            val intent = Intent(context, NotificationActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, intent, 0)

            var builder : NotificationCompat.Builder = NotificationCompat.Builder(context!!, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_baseline_fitness_center_24)
                    .setContentTitle("알람")
                    .setContentText("${memo}")
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val importance = NotificationManager.IMPORTANCE_HIGH
                val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                    description = CHANNEL_DESCRIPTION
                }
                // Register the channel with the system
                val notificationManager: NotificationManager =
                        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.createNotificationChannel(channel)
                notificationManager.notify(0, builder.build())
            }
        }

    }
}