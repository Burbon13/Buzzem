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
import android.os.VibrationEffect
import android.os.Vibrator
import android.support.v4.app.NotificationCompat
import com.burbon13.buzzem.R
import java.security.Policy

class MyNotification(context:Context) : ContextWrapper(context){

    val manager:NotificationManager by lazy {
        getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    init {
        if(Build.VERSION.SDK_INT >= 26) {
            val aAtr = AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .build()
            val channel1 = NotificationChannel(FIRST_CHANNEL, "Buzz channel HAHA", NotificationManager.IMPORTANCE_HIGH)
            channel1.lightColor = Color.BLUE
            channel1.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            channel1.setSound(Uri.parse("android.resource://"+applicationContext.packageName+"/"+R.raw.pika),aAtr)
            //channel1.enableVibration(true)
            //channel1.vibrationPattern = longArrayOf(100,200,300,400,500,400,300,200,400)
            manager.createNotificationChannel(channel1)
        }
    }

    fun getBuzzNotification(title:String, body:String) : Notification.Builder {
        if(Build.VERSION.SDK_INT >= 26)
            return Notification.Builder(applicationContext, FIRST_CHANNEL)
                    //.setVibrate(longArrayOf(0,5000,-1)) //Deprecated, couldn't get the channel1.vibrationPattern to work!!
                    .setContentText(body)
                    .setContentTitle(title)
                    .setSmallIcon(R.drawable.caution_sign)
        else
            return Notification.Builder(applicationContext)
                    //.setVibrate(longArrayOf(0,5000,-1))
                    .setContentText(body)
                    .setContentTitle(title)
                    .setSmallIcon(R.drawable.caution_sign)
                    .setSound(Uri.parse("android.resource://"+applicationContext.packageName+"/"+R.raw.pika))
    }

    fun notify(id:Int, notification:Notification.Builder) {
        //Remember to save the notification ID that you pass to NotificationManagerCompat.notify()
        // because you'll need it later if you want to update or remove the notification.
        manager.notify(id,notification.build())

        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        if(Build.VERSION.SDK_INT >= 26) {
            vibrator.vibrate(VibrationEffect.createOneShot(5000, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            vibrator.vibrate(5000)
        }

        if(Build.VERSION.SDK_INT >= 23) {
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
                Thread.sleep(300)
                camerasWithFlash.forEach {
                    cameraManager.setTorchMode(it,false)
                }
                Thread.sleep(200)
            }
        } else {
            if(packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
                val cam = android.hardware.Camera.open()
                val p = cam.parameters
//                p.flashMode = android.hardware.Camera.Parameters.FLASH_MODE_TORCH
//                cam.parameters = p
//                cam.startPreview()

                for(step in 1..10) {
                    p.flashMode = android.hardware.Camera.Parameters.FLASH_MODE_TORCH
                    cam.parameters = p
                    cam.startPreview()
                    Thread.sleep(300)
                    p.flashMode = android.hardware.Camera.Parameters.FLASH_MODE_OFF
                    cam.parameters = p
                    cam.startPreview()
                    Thread.sleep(200)
                }
            }
        }
    }

    companion object {
        val FIRST_CHANNEL = "buzz"
    }
}