package com.burbon13.buzzem.activities

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.content.ContextCompat.startActivity
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class BuzzBroadcastReceiver : BroadcastReceiver() {
    private val tag = "BuzzBroadcastReceiver" //tag for logs
    private val sharedPrefName = "buzz_app_settings" //name for our sharedPreferences

    //method called when broadcast is received
    override fun onReceive(context: Context?, intent: Intent?) {
        if(intent?.action.equals("com.buzz.notification")) { //checking if it is our wanted broadcast
            //loading the bundle to search for notifications
            val bundle = intent?.extras
            val myUID = bundle?.get("uid") as String
            val myHashSet = intent.getSerializableExtra("friends") as HashSet<String>

            //getting database reference
            val dbRef = FirebaseDatabase.getInstance().reference.child("buzzez").child(myUID)

            dbRef.addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onCancelled(p0: DatabaseError) {}

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val children = dataSnapshot.children
                    var usersBuzzes = ""

                    //iterate through buzzes
                    children.forEach {
                        if(myHashSet.contains(it.key.toString())) //if it is one of my friends
                            usersBuzzes += it.value.toString() + "\n" //add him
                    }

                    //reset the db node to default
                    dbRef.setValue(true)

                    //if we have valid buzzes then notify the user
                    if(usersBuzzes != "")
                        notifyUser(context,usersBuzzes)

                }

            })

        }
    }

    private fun notifyUser(context:Context?, usersBuzzes:String) {
        //get the sharedPreferences to see how to manipulate our buzzes
        val sharedPref = context!!.getSharedPreferences(sharedPrefName, Context.MODE_PRIVATE)

        //loading the current settings
        val timeNotification = sharedPref.getLong("notification_miliseconds", 5001)
        val flashEnabled = sharedPref.getBoolean("flash_enabled", true)
        val vibrationEnabled = sharedPref.getBoolean("vibration_enabled", true)
        val wakeUpScreenEnabled = sharedPref.getBoolean("wake_up_screen_enabled", true)
        val notificationEnabled = sharedPref.getBoolean("notification_enabled", true)

        val bundle = Bundle()
        bundle.putLong("notification_miliseconds", timeNotification)
        bundle.putBoolean("flash_enabled",flashEnabled)
        bundle.putBoolean("vibration_enabled",vibrationEnabled)
        bundle.putBoolean("notification_enabled",notificationEnabled)

        val asyncNotification = AsynchronousNotification(context,usersBuzzes,bundle)
        asyncNotification.start()

        //conditional waking screen
        if(wakeUpScreenEnabled) {
            val intentNew = Intent(context, BuzzNotification::class.java)
            intentNew.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intentNew)
        }
    }

    inner class AsynchronousNotification(var context: Context, var strTS:String, var bundle:Bundle): Thread() {

        override fun run() {
            val myNotification = MyNotification(context,bundle)
            myNotification.notify(1,myNotification.getBuzzNotification("New Buzz",strTS))
        }
    }
}