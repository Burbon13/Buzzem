package com.burbon13.buzzem.activities

import android.app.Activity
import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
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
import com.google.firebase.functions.FirebaseFunctions
import data.User
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.contact_ticket.view.*
import java.util.*
import java.util.concurrent.locks.ReentrantLock
import kotlin.collections.HashMap
import kotlin.collections.HashSet
import com.google.android.gms.tasks.Continuation
import com.google.firebase.functions.HttpsCallableResult
import com.google.android.gms.tasks.Task
import com.google.firebase.functions.FirebaseFunctionsException


class MainActivity : AppCompatActivity() {

    private val myFriendsList = ArrayList<User>()
    private val myFriendsHashMap = HashSet<String>()
    private var myRef = FirebaseDatabase.getInstance().reference
    private var mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val adapter = FriendsAdapter(myFriendsList)
    private val TAG = "MainActivity"
    private val TAG_FNC = "MainActivityFNC"
    private val lock = ReentrantLock()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //FirebaseDatabase.getInstance().setLogLevel(Logger.Level.DEBUG)
        //myRef = FirebaseDatabase.getInstance().reference

        //backend fun


        setDataSharedPref()
        lvContacts.adapter = adapter
        Log.d(TAG, "loadFriends()")
        loadFriends()
    }

    private fun playWithTheBackend() {
        addMessage("hehehehePUALA")

    }

    private fun addMessage(text: String) {
        val data = HashMap<String, Any>()
        data.put("text", text)
        data.put("push", true)

        val mFunctions = FirebaseFunctions.getInstance()
        mFunctions.getHttpsCallable("addMessageApp")
                .call(data)
                .continueWith{
                    if(it.isSuccessful) {
                        Log.d(TAG_FNC, "https call good")
                    } else {
                        Log.d(TAG_FNC, "https call FUCKING BAD")
                        Log.d(TAG_FNC, it.exception.toString())
                    }
                }
    }


    private fun setDataSharedPref() {
        val sharedPref = getSharedPreferences("buzz_app_settings", Context.MODE_PRIVATE)
        //Code which runs only once
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
        clearNotifications()
    }

    private fun clearNotifications() {
        //In case you are using this in the Main Thread and you are sure that it’s not going
        // to be used in different threads, then you can avoid all of this
        // overhead to make it Thread Safe and just use it like this:
        val notificationManager by lazy (LazyThreadSafetyMode.NONE) {
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager }
        //And that’s it! you are using a faster implementation of the Lazy Delegated Property.
        notificationManager.cancel(1)

    }

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
                Log.d(TAG, "startActivityForResult()")
                intent.putExtra("my_email", mAuth.currentUser?.email.toString())
                intent.putExtra("friends", myFriendsHashMap)
                startActivityForResult(intent,1234)
            }
            R.id.settingsItem -> {
                //val intent = Intent(this, SettingsActivity::class.java)
                //startActivity(intent)
                playWithTheBackend()
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
