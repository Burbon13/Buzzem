package com.burbon13.buzzem.activities

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.PackageManager
import android.graphics.Camera
import android.graphics.Color
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.support.v4.app.NotificationCompat
import com.burbon13.buzzem.R
import java.security.Policy

class MyNotification(context:Context, var bundle:Bundle) : ContextWrapper(context){

    private var timeNotification:Long = 0
    private var flashEnabled:Boolean = false
    private var vibrationEnabled:Boolean = false
    private var notificationEnabled:Boolean = false
    private val manager:NotificationManager by lazy {
        getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    init {
        //Get values from bundle
        timeNotification = bundle.getLong("notification_miliseconds", 5001)
        flashEnabled = bundle.getBoolean("flash_enabled", true)
        vibrationEnabled = bundle.getBoolean("vibration_enabled", true)
        notificationEnabled = bundle.getBoolean("notification_enabled", true)


        //Setup the channel for notifications
        if(Build.VERSION.SDK_INT >= 26) {
            val aAtr = AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .build()
            val channel1 = NotificationChannel(FIRST_CHANNEL, "Buzz channel HAHA", NotificationManager.IMPORTANCE_HIGH)
            channel1.lightColor = Color.BLUE
            channel1.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            channel1.setSound(Uri.parse("android.resource://"+applicationContext.packageName+"/"+R.raw.pika),aAtr)
            manager.createNotificationChannel(channel1)
        }
    }

    fun getBuzzNotification(title:String, body:String) : Notification.Builder {
        if(Build.VERSION.SDK_INT >= 26)
            return Notification.Builder(applicationContext, FIRST_CHANNEL)
                    .setContentText(body)
                    .setContentTitle(title)
                    .setSmallIcon(R.drawable.caution_sign)

        return Notification.Builder(applicationContext)
                .setContentText(body)
                .setContentTitle(title)
                .setSmallIcon(R.drawable.caution_sign)
                .setSound(Uri.parse("android.resource://"+applicationContext.packageName+"/"+R.raw.pika))
    }

    fun notify(id:Int, notification:Notification.Builder) {
        //Remember to save the notification ID that you pass to NotificationManagerCompat.notify()
        // because you'll need it later if you want to update or remove the notification.
        if(notificationEnabled)
            manager.notify(id,notification.build())

        if(vibrationEnabled)
            makeMeVibrate()

        if(flashEnabled)
            makeMeFlash()
    }

    private fun makeMeVibrate() {
        val asyncVibrator = AsyncVibrate()
        asyncVibrator.start()
    }

    inner class AsyncVibrate : Thread() {
        override fun run() {

            val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

            if(Build.VERSION.SDK_INT >= 26) {
                vibrator.vibrate(VibrationEffect.createOneShot(timeNotification, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                vibrator.vibrate(timeNotification)
            }
        }
    }

    private fun makeMeFlash() {
        if(Build.VERSION.SDK_INT >= 23) {
            val asyncFlash = AsyncFlashNewer()
            asyncFlash.start()
        } else {
            val asyncFlash = AsyncFlashOlder()
            asyncFlash.start()
        }
    }

    inner class AsyncFlashNewer : Thread() {
        override fun run() {
            if(Build.VERSION.SDK_INT < 23)
                return

            val cameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
            val cameras = cameraManager.cameraIdList
            val camerasWithFlash = ArrayList<String>()
            cameras.forEach {
                if(cameraManager.getCameraCharacteristics(it).get(CameraCharacteristics.FLASH_INFO_AVAILABLE) == true)
                    camerasWithFlash.add(it)
            }

            for(step in 1..10) {
                camerasWithFlash.forEach {
                    cameraManager.setTorchMode(it,true)
                }
                Thread.sleep(timeNotification/20)
                camerasWithFlash.forEach {
                    cameraManager.setTorchMode(it,false)
                }
                Thread.sleep(timeNotification/20)
            }
        }
    }

    inner class AsyncFlashOlder : Thread() {
        override fun run() {
            if(packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
                val cam = android.hardware.Camera.open()
                val p = cam.parameters

                for(step in 1..10) {
                    p.flashMode = android.hardware.Camera.Parameters.FLASH_MODE_TORCH
                    cam.parameters = p
                    cam.startPreview()
                    Thread.sleep(timeNotification/20)
                    p.flashMode = android.hardware.Camera.Parameters.FLASH_MODE_OFF
                    cam.parameters = p
                    cam.startPreview()
                    Thread.sleep(timeNotification/20)
                }
            }
        }
    }

    companion object {
        val FIRST_CHANNEL = "buzz"
    }
}