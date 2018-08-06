package com.burbon13.buzzem.activities

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.burbon13.buzzem.R

class MainActivity : AppCompatActivity() {
    //Declare instance of FirebaseAuth
   // private var mAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Initialize FirebaseAuth instance
       // mAuth = FirebaseAuth.getInstance()
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
                //finish()
                //overridePendingTransition(0,0)
                startActivity(intent)
            }
            R.id.settingsItem -> {
                Toast.makeText(this,"Settings",Toast.LENGTH_LONG).show()
            }
        }

        return super.onOptionsItemSelected(item)
    }
}
