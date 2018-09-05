package com.burbon13.buzzem.activities

import android.content.Context
import android.content.DialogInterface
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.ContextThemeWrapper
import android.view.Menu
import android.view.MenuItem
import android.widget.SeekBar
import android.widget.Toast
import com.burbon13.buzzem.R
import kotlinx.android.synthetic.main.activity_notifications_settings.*

class NotificationsSettingsActivity : AppCompatActivity() {
    private val tag = "NSActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notifications_settings)

        setContentFromSharedPref()
        setListenersForSwitches()
    }

    private fun setContentFromSharedPref() {
        val sharedPref = getSharedPreferences("buzz_app_settings", Context.MODE_PRIVATE)
        if(!sharedPref.getBoolean("flash_enabled", true)) {
            switchFlash.isChecked = false
        }

        if(!sharedPref.getBoolean("vibration_enabled", true)) {
            switchVibration.isChecked = false
        }

        if(!sharedPref.getBoolean("wake_up_screen_enabled", true)) {
            switchWake.isChecked = false
        }

        if(!sharedPref.getBoolean("notification_enabled", true)) {
            switchNotif.isChecked = false
        }

        val duration = sharedPref.getLong("notification_miliseconds", 5000) / 1000
        seekBarBuzz.progress = duration.toInt()
        //Consider using place holders instead of hardcoding the string
        tvBuzz.text = resources.getString(R.string.notification_miliseconds) + ": " + duration.toString() + " " + resources.getString(R.string.seconds)
    }

    private fun setListenersForSwitches() {
        val sharedPref = getSharedPreferences("buzz_app_settings", Context.MODE_PRIVATE)
        switchFlash.setOnClickListener {
            sharedPref.edit().putBoolean("flash_enabled", switchFlash.isChecked).apply()
            Log.d(tag, "set flash_enabled " + switchFlash.isChecked.toString())
        }

        switchVibration.setOnClickListener {
            sharedPref.edit().putBoolean("vibration_enabled", switchVibration.isChecked).apply()
            Log.d(tag, "set vibration_enabled " + switchVibration.isChecked.toString())
        }

        switchWake.setOnClickListener {
            sharedPref.edit().putBoolean("wake_up_screen_enabled", switchWake.isChecked).apply()
            Log.d(tag, "set wake_up_screen_enabled " + switchWake.isChecked.toString())
        }

        switchNotif.setOnClickListener {
            sharedPref.edit().putBoolean("notification_enabled", switchNotif.isChecked).apply()
            Log.d(tag, "set notification_enabled " + switchNotif.isChecked.toString())
        }

        seekBarBuzz.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {

            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                tvBuzz.text = resources.getString(R.string.notification_miliseconds) + ": " + progress.toString() + " " + resources.getString(R.string.seconds)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                if(seekBar == null) {
                    Toast.makeText(applicationContext, "Error occurred", Toast.LENGTH_LONG).show()
                    return
                }

                Toast.makeText(applicationContext, "Duration set to " + seekBar.progress + " seconds", Toast.LENGTH_LONG).show()
                sharedPref.edit().putLong("notification_miliseconds", seekBar.progress.toLong() * 1000).apply()
            }

        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.notifications_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId) {
            R.id.itemQuestion -> {
                createAlertDialogForQuestions()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun createAlertDialogForQuestions() {
        val builder = AlertDialog.Builder(ContextThemeWrapper(this, R.style.AlertDialogNotificationsSettings))
        builder.setTitle(R.string.notif_item_title).setMessage(R.string.notif_item_message)
                .setPositiveButton(R.string.notif_item_button, DialogInterface.OnClickListener { dialog, id -> })
        builder.create().show()
    }
}