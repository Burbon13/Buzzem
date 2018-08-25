package com.burbon13.buzzem.activities

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.burbon13.buzzem.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import data.decodeString
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.contact_ticket.view.*
import java.util.*

class MainActivity : AppCompatActivity() {

    private val myFriendsList = ArrayList<String>()
    private val myRef = FirebaseDatabase.getInstance().reference
    private var mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val adapter = FriendsAdapter(myFriendsList)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        lvContacts.adapter = adapter
        loadFriends()
    }

    private fun loadFriends() {
        myRef.child("friends").child(mAuth.uid.toString()).addListenerForSingleValueEvent(
                object: ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {
                        return
                    }

                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val children = dataSnapshot.children
                        children.forEach {
                            myFriendsList.add(it.key.toString())
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
                startActivity(intent)
            }
            R.id.settingsItem -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
            }
        }

        return super.onOptionsItemSelected(item)
    }

    inner class FriendsAdapter(var myFriends:ArrayList<String>) : BaseAdapter() {
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val view = layoutInflater.inflate(R.layout.contact_ticket,null)
            view.tvName.text = decodeString(myFriends[position])

            view.setOnClickListener {
                val intent = Intent(applicationContext, BuzzActivity::class.java)
                intent.putExtra("email", decodeString(myFriends[position]))
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
