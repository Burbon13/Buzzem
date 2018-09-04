package com.burbon13.buzzem.activities

import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.burbon13.buzzem.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import data.User
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.contact_ticket.view.*
import java.util.*
import java.util.concurrent.locks.ReentrantLock
import kotlin.collections.HashSet

class MainActivity : AppCompatActivity() {

    private val myFriendsList = ArrayList<User>()
    private val myFriendsHashMap = HashSet<String>()
    private var myRef = FirebaseDatabase.getInstance().reference
    private var mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val adapter = FriendsAdapter(myFriendsList)
    val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //FirebaseDatabase.getInstance().setLogLevel(Logger.Level.DEBUG)
        //myRef = FirebaseDatabase.getInstance().reference

        setDataSharedPref()
        lvContacts.adapter = adapter
        Log.d(TAG, "loadFriends()")
        loadFriends()
    }

    private fun setDataSharedPref() {
//        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
//        if(!prefs.getBoolean("first_time", false)) {
//            //One lifetime code
//            Log.d(TAG, "One lifetime run")
//
//            val editor = prefs.edit()
//            editor.putInt("time_for_notification", 5000)
//
//            //Set it to not run again
//            editor.putBoolean("first_time", true)
//            editor.apply()
//        }

        val sharedPref = getSharedPreferences("buzz_app_settings", Context.MODE_PRIVATE)
        if(!sharedPref.getBoolean("first_time", false)) {
            Log.d(TAG, "Shared pref setup")

            val editor = sharedPref.edit()
            editor.putBoolean("first_time", true)
            editor.putLong("notification_miliseconds", 5000)
            editor.putBoolean("flash_enabled", true)
            editor.putBoolean("vibration_enabled", true)
            editor.putBoolean("wake_up_screen_enabled", true)
            editor.putBoolean("notification_enabled", true)
            editor.apply()
        }
    }

    fun setAlarmManager() {
        val calendar = Calendar.getInstance()
        val alarmManager = applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(applicationContext,BuzzBroadcastReceiver::class.java)
        intent.action = "com.buzz.notification"
        intent.putExtra("uid",mAuth.uid)
        Log.d(TAG, "myFirendsHashMap.size = " + myFriendsHashMap.size.toString())
        intent.putExtra("friends", myFriendsHashMap)

        val pendingIntent = PendingIntent.getBroadcast(applicationContext,0,intent,PendingIntent.FLAG_UPDATE_CURRENT)

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,calendar.timeInMillis,60000,pendingIntent)
    }

    override fun onResume() {
        super.onResume()
        //TODO: From time to time to verify for new friends
        //loadFriends()
    }

    private val lock = ReentrantLock()

    private fun loadFriends() {
        myRef.child("friends").child(mAuth.uid.toString()).addValueEventListener(
                object: ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {
                        Log.d(TAG, "Canceled ")
                        return
                    }

                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val children = dataSnapshot.children
                        myFriendsList.clear()
                        var many = dataSnapshot.childrenCount
                        Log.d(TAG, "childrenCount = " + many.toString())
                        children.forEach {
                            Log.d(TAG, "Loop: " + it.key.toString())
                            myRef.child("users").child(it.key.toString()).addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onCancelled(p0: DatabaseError) {
                                    return
                                }

                                override fun onDataChange(dataSnapshot: DataSnapshot) {
                                    val email = dataSnapshot.child("email").value.toString()

                                    if(email == "null") {
                                        Log.d(TAG, "Not my friend!")
                                        myRef.child("friends").child(mAuth.uid.toString()).child(it.key.toString()).removeValue()
                                    } else {
                                        lock.lock()
                                        myFriendsList.add(User(email,it.key.toString()))
                                        myFriendsHashMap.add(it.key.toString())
                                        Log.d(TAG, "email got " + email)
                                        many --

                                        if(many == 0L) {
                                            adapter.notifyDataSetChanged()
                                            setAlarmManager()
                                            Log.d(TAG, "notifyDataSetChanged()")
                                        }
                                        lock.unlock()
                                    }
                                }
                            })
                        }
                        adapter.notifyDataSetChanged()
                    }
                }
        )
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.main_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId) {
            R.id.addItem -> {
                val intent = Intent(this, SearchActivity::class.java)
                //startActivity(intent)
                Log.d(TAG, "startActivityForResult()")
                startActivityForResult(intent,1234)
            }
            R.id.settingsItem -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.d(TAG, "onActivityResult")
        if(requestCode == 1234) {
            if(resultCode == Activity.RESULT_OK) {
                setAlarmManager()
            }
        }
    }

    inner class FriendsAdapter(var myFriends:ArrayList<User>) : BaseAdapter() {
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val view = layoutInflater.inflate(R.layout.contact_ticket,null)
            view.tvName.text = myFriends[position].email

            view.setOnClickListener {
                val intent = Intent(applicationContext, BuzzActivity::class.java)
                intent.putExtra("email", myFriends[position].email)
                intent.putExtra("uid", myFriends[position].uid)
                intent.putExtra("myUid", mAuth.uid)
                intent.putExtra("myEmail", mAuth.currentUser?.email)
                startActivity(intent)
            }

            return view
        }

        override fun getItem(position: Int): Any {
            return myFriends[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getCount(): Int {
            return myFriends.size
        }

    }
}
