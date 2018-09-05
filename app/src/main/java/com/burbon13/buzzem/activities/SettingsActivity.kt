package com.burbon13.buzzem.activities

import android.content.DialogInterface
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.ContextThemeWrapper
import android.view.View
import android.widget.Toast
import com.burbon13.buzzem.R
import com.google.firebase.auth.FirebaseAuth

class SettingsActivity : AppCompatActivity() {

    private val mAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
    }

    fun logoutEvent(view: View) {
        val builder = AlertDialog.Builder(ContextThemeWrapper(this,R.style.AlertDialogNotificationsSettings))
        builder.setTitle("Logout").setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes", DialogInterface.OnClickListener { dialog, which ->
                    mAuth.signOut()
                    val intent = Intent(this,LoginActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
                })
                .setNegativeButton("No", DialogInterface.OnClickListener { dialog, which ->  })
        builder.create().show()
    }

    fun accountEvent(view:View) {
        val intent = Intent(this, AccountActivity::class.java)
        startActivity(intent)
    }

    fun notificationsEvent(view:View) {
        val intent = Intent(this, NotificationsSettingsActivity::class.java)
        startActivity(intent)
    }
}
