package com.burbon13.buzzem.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import com.burbon13.buzzem.R

class SearchActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.search_menu,menu)
        return super.onCreateOptionsMenu(menu)

        //Ramas aici inainte de Nisa
        //TODO: To implement the search bar to work for searching contacts into the DB
    }
}
