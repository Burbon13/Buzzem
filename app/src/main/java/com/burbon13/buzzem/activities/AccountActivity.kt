package com.burbon13.buzzem.activities

import android.content.DialogInterface
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.ContextThemeWrapper
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.burbon13.buzzem.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class AccountActivity : AppCompatActivity() {
    private val TAG = "AccountActivity"
    private var mAuth:FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account)
        mAuth = FirebaseAuth.getInstance()
    }

    fun resetPassEvent(view: View) {
        mAuth.sendPasswordResetEmail(mAuth.currentUser?.email.toString())
                .addOnCompleteListener {
                    if(it.isSuccessful) {
                        Toast.makeText(applicationContext,"Password reset email sent!", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(applicationContext, "Error occured", Toast.LENGTH_LONG).show()
                        Log.e(TAG, it.exception.toString())
                    }
                }
    }

    fun deleteEvent(view:View) {
        val uid = mAuth.uid.toString()

        val builder = AlertDialog.Builder(ContextThemeWrapper(this,R.style.AlertDialogDeleteAccount))
        builder.setTitle(R.string.delete_account_alert_title)
        builder.setCancelable(false)
        builder.setMessage(R.string.delete_account_alert_text)
                .setPositiveButton("Yes", DialogInterface.OnClickListener { dialog, id ->
                    mAuth.currentUser?.delete()?.addOnCompleteListener {
                        if(it.isSuccessful) {
                            //Maybe add a password confirmation
                            Toast.makeText(applicationContext,"User deleted", Toast.LENGTH_LONG).show()
                            deleteFromDB(uid)
                            val intent = Intent(this,LoginActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            startActivity(intent)
                        } else {
                            Toast.makeText(applicationContext, "You must sign in again in order to delete your account", Toast.LENGTH_LONG).show()
                            Log.e(TAG, it.exception.toString())
                        }
                    }
                })
                .setNegativeButton("No", DialogInterface.OnClickListener { dialog, id ->

                })

        val alertDialog = builder.create()
        alertDialog.show()
    }

    fun deleteFromDB(uid:String) {
        val dbRef = FirebaseDatabase.getInstance().reference

        dbRef.child("buzzez").child(uid).removeValue()
        dbRef.child("friends").child(uid).removeValue()
        dbRef.child("users").child(uid).removeValue()
    }

    fun changeEmailEvent(view:View) {
        //Bug when trying to log back

        val editText = EditText(this)
        val builder = AlertDialog.Builder(ContextThemeWrapper(this,R.style.AlertDialogDeleteAccount))
        builder.setTitle(R.string.reset_email_alert_title)
        builder.setCancelable(false)
        builder.setView(editText)
        builder.setMessage(R.string.reset_email_alert_text)
                .setPositiveButton("Submit", DialogInterface.OnClickListener { dialog, id ->
                    mAuth.currentUser?.updateEmail(editText.text.toString())?.addOnCompleteListener {
                        if(it.isSuccessful) {
                            Toast.makeText(applicationContext,"Email successfully reset",Toast.LENGTH_LONG).show()
                            val dbRef = FirebaseDatabase.getInstance().reference
                            dbRef.child("users").child(mAuth.uid.toString()).child("email").setValue(editText.text.toString())
                            mAuth.currentUser?.sendEmailVerification()
                            mAuth.signOut()
                            val intent = Intent(this, LoginActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                        } else {
                            Toast.makeText(applicationContext,"Error occurred", Toast.LENGTH_LONG).show()
                            Log.e(TAG, it.exception.toString())
                        }
                    }
                })
                .setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, id -> })

        builder.create().show()
    }
}
