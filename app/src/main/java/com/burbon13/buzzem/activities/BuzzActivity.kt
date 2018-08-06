package com.burbon13.buzzem.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import com.burbon13.buzzem.R

class BuzzActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_buzz)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.buzz_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }
}
