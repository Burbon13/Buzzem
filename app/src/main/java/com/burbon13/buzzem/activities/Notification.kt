package com.burbon13.buzzem.activities

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Color
import android.os.Build
import android.support.v4.app.NotificationCompat
import com.burbon13.buzzem.R

class MyNotification(context:Context) : ContextWrapper(context){

    val manager:NotificationManager by lazy {
        getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    init {
        if(Build.VERSION.SDK_INT >= 26) {
            val channel_1 = NotificationChannel(FIRST_CHANNEL, "Buzz channel", NotificationManager.IMPORTANCE_HIGH)
            channel_1.lightColor = Color.BLUE
            channel_1.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            manager.createNotificationChannel(channel_1)
        }
    }

    fun getBuzzNotification(title:String, body:String) : Notification.Builder {
        if(Build.VERSION.SDK_INT >= 26)
            return Notification.Builder(applicationContext, FIRST_CHANNEL)
                    .setContentText(body)
                    .setContentTitle(title)
                    .setSmallIcon(R.drawable.caution_sign)
        else
            return Notification.Builder(applicationContext)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setContentText(body)
                    .setContentTitle(title)
                    .setSmallIcon(R.drawable.caution_sign)
    }

    fun notify(id:Int, notification:Notification.Builder) {
        //Remember to save the notification ID that you pass to NotificationManagerCompat.notify()
        // because you'll need it later if you want to update or remove the notification.
        manager.notify(id,notification.build())
    }

    companion object {
        val FIRST_CHANNEL = "buzz"
    }
}