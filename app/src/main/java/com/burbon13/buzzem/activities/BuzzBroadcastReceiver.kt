package com.burbon13.buzzem.activities

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class BuzzBroadcastReceiver : BroadcastReceiver() {

    val TAG = "BuzzBroadcastReceiver"

    override fun onReceive(context: Context?, intent: Intent?) {
        if(intent?.action.equals("com.buzz.notification")) {

            //Log.d(TAG, "onReceive()")
            val bundle = intent?.extras

            val myUID = bundle?.get("uid") as String
            //Log.d(TAG, "UID: " + myUID)

            val dbRef = FirebaseDatabase.getInstance().reference.child("buzzez").child(myUID)


            dbRef.addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onCancelled(p0: DatabaseError) {
                    return
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {

                    val children = dataSnapshot.children
                    var strTS = ""

                   // Log.d(TAG, "onDatChange")
                    children.forEach {
                        strTS += it.key.toString() + "\n"
                    }


                    if(strTS != "") {
                        //Log.d(TAG, "String to notify: " +  strTS)
                        val myNotification = MyNotification(context!!)
                        myNotification.notify(1,myNotification.getBuzzNotification("New Buzz",strTS))
                    }

                    dbRef.setValue(true)
                    //Log.d(TAG, "Reset DB")
                }

            })

        }
    }

}