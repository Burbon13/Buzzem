package com.burbon13.buzzem.activities

import android.content.Context
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.Menu
import android.widget.Toast
import com.burbon13.buzzem.R
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_buzz.*

class BuzzActivity : AppCompatActivity() {

    var friendsUid:String = ""
    var myUid:String = ""
    var myEmail:String = ""
    val dbRef = FirebaseDatabase.getInstance().reference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_buzz)

        setDataFromExtras()
        setBuzzButton()
    }

    fun setDataFromExtras() {
        val extras = intent.extras
        tvNameToBuzz.text = extras.getString("email")
        friendsUid = extras.getString("uid")
        myUid = extras.getString("myUid")
        myEmail = extras.getString("myEmail")
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.buzz_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    fun setBuzzButton() {
        ivBuzz.setOnClickListener {
            //Toast.makeText(applicationContext,friendsUid,Toast.LENGTH_LONG).show()
            dbRef.child("buzzez").child(friendsUid).child(myUid).setValue(myEmail)

            val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

            if(Build.VERSION.SDK_INT >= 26) {
                vibrator.vibrate(VibrationEffect.createOneShot(232,VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                vibrator.vibrate(232)
            }
        }
    }
}
