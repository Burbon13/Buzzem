package com.burbon13.buzzem.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import com.burbon13.buzzem.R

class BuzzNotification : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_buzz_notification)
    }

    override fun onStart() {
        super.onStart()
        //finishInSeconds()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED)
        window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON)
    }

    fun finishInSeconds() {
        Thread.sleep(6000)
        finish()
    }
}
