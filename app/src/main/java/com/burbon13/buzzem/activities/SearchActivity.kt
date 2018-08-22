package com.burbon13.buzzem.activities

import android.app.SearchManager
import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.ConditionVariable
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.burbon13.buzzem.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.android.synthetic.main.search_ticket.view.*

class SearchActivity : AppCompatActivity() {

    private val myRef = FirebaseDatabase.getInstance().reference
    private val usersList = ArrayList<String>()
    private val userListFromQuery = ArrayList<String>()
    private val adapter = MySearchAdapter(userListFromQuery)
    private val TAG = "SearchActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        getUsers()
        lvSearchedContacts.adapter = adapter
    }

    private fun getUsers() {
        Log.d(TAG, "Getting users from database")
        myRef.child("users").addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                //Toast.makeText(applicationContext,"Canceled",Toast.LENGTH_LONG).show()
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                //save to usersList
                usersList.clear()
                val children = dataSnapshot.children
                children.forEach {
                    //Toast.makeText(applicationContext,it.value.toString(),Toast.LENGTH_LONG).show()
                    usersList.add(it.value.toString())
                    Log.d(TAG, "User added: " + it.value.toString())
                }
            }

        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.search_menu,menu)


        val sView = menu?.findItem(R.id.my_menu_search)?.actionView as SearchView
        val sManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        sView.setSearchableInfo(sManager.getSearchableInfo(componentName))

        //May need update when there are too many users
        sView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                Log.d(TAG, "Query submitted")

                if(query == null) {
                    Log.d(TAG, "Query is null, return false")
                    return false
                }

                Log.d(TAG, "Populating the list")
                userListFromQuery.clear()
                for(email in usersList) {
                    if(email.contains(query))
                        userListFromQuery.add(email)
                }

                Log.d(TAG, "userListFromQuery size is " + userListFromQuery.size)
                Log.d(TAG, "Notifying data change")
                adapter.notifyDataSetChanged()

                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                //Does nothing!
                return true
            }

        })

        return super.onCreateOptionsMenu(menu)
    }

    inner class MySearchAdapter(var localUserList:ArrayList<String>) : BaseAdapter() {
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val view = layoutInflater.inflate(R.layout.search_ticket,null)
            view.tvSearchName.text = localUserList[position]
            view.setOnClickListener {
                Toast.makeText(applicationContext,"Adding " + localUserList[position],Toast.LENGTH_LONG).show()
                //TODO: Implement adding friends
            }
            return view
        }

        override fun getItem(position: Int): Any {
            return position
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getCount(): Int {
            return localUserList.size
        }

    }
}
