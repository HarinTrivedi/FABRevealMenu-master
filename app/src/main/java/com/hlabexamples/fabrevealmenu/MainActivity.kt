package com.hlabexamples.fabrevealmenu

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        val navigation = findViewById<BottomNavigationView>(R.id.navigation)
        navigation.setOnNavigationItemSelectedListener(this)

        showXmlFragment()

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        val id = item.itemId

        when (id) {
            R.id.nav_xml -> {
                showXmlFragment()
                return true
            }
            R.id.nav_code -> {
                showCodeFragment()
                return true
            }
            R.id.nav_custom -> {
                val intent = Intent(this@MainActivity, ScrollingActivity::class.java)
                startActivity(intent)
                return true
            }
            else -> return false
        }
    }

    private fun showXmlFragment() {
        supportFragmentManager.beginTransaction().replace(R.id.container, DemoXmlFragment()).commit()
    }

    private fun showCodeFragment() {
        supportFragmentManager.beginTransaction().replace(R.id.container, DemoCodeFragment()).commit()
    }

}
