package com.burbon13.buzzem.activities

import android.app.Activity
import android.app.AlertDialog
import android.app.SearchManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.ConditionVariable
import android.util.Log
import android.view.*
import android.widget.*
import com.burbon13.buzzem.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import data.User
import data.encodeString
import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.android.synthetic.main.search_ticket.view.*

class SearchActivity : AppCompatActivity() {

    private val myRef = FirebaseDatabase.getInstance().reference
    private val usersList = ArrayList<User>()
    private val userListFromQuery = ArrayList<User>()
    private val adapter = MySearchAdapter(userListFromQuery)
    private val TAG = "SearchActivity"
    private var myEmail = ""
    private var friendsHashSet = HashSet<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        loadMyData()
        getUsers()
        lvSearchedContacts.adapter = adapter
    }

    private fun loadMyData() {
        val bundle = intent.extras
        myEmail = bundle.getString("my_email")
        friendsHashSet = intent.getSerializableExtra("friends") as HashSet<String>
    }

    private fun getUsers() {
        Log.d(TAG, "Getting users from database")
        myRef.child("users").addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {}

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                //save to usersList
                usersList.clear()
                val children = dataSnapshot.children
                children.forEach {
                    if(!friendsHashSet.contains(it.key))
                        usersList.add(User(it.child("email").value.toString(),it.key.toString()))
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
                    return true
                }

                if(query.length < 5) {
                    Toast.makeText(applicationContext, "Search string must contain at least 5 characters", Toast.LENGTH_LONG).show()
                    return true
                }

                Log.d(TAG, "Populating the list")
                userListFromQuery.clear()
                for(user in usersList) {
                    if(user.email.contains(query) && user.email != myEmail)
                        userListFromQuery.add(user)
                    if(userListFromQuery.size >= 20)
                        break
                }

                Log.d(TAG, "userListFromQuery size is " + userListFromQuery.size)
                Log.d(TAG, "Notifying data change")
                adapter.notifyDataSetChanged()

                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                //Does nothing!
                return true
            }

        })

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId) {
            R.id.search_questions -> {
                popUpAlertDialogForInfo()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun popUpAlertDialogForInfo() {
        val builder = AlertDialog.Builder(ContextThemeWrapper(this,R.style.AlertDialogNotificationsSettings))
        builder.setTitle(R.string.to_know)
        builder.setMessage(R.string.to_know_search)
        builder.setPositiveButton(R.string.yes_sir, DialogInterface.OnClickListener { dialog, which -> })
        builder.create().show()
    }

    inner class MySearchAdapter(var localUserList:ArrayList<User>) : BaseAdapter() {
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val view = layoutInflater.inflate(R.layout.search_ticket,null)
            view.tvSearchName.text = localUserList[position].email
            view.setOnClickListener {

                val email = localUserList[position]

                val mAuth: FirebaseAuth = FirebaseAuth.getInstance()

                myRef.child("friends").child(mAuth.uid.toString()).child(localUserList[position].uid).setValue(true)
                Toast.makeText(applicationContext, localUserList[position].email + " added",Toast.LENGTH_LONG).show()

                val data = Intent()
                data.putExtra("was_added", "1")
                Log.d(TAG, "RESULT_OK " + Activity.RESULT_OK.toString())
                setResult(Activity.RESULT_OK, data)
                finish()
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
