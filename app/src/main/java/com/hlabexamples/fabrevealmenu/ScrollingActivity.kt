package com.hlabexamples.fabrevealmenu

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_scrolling.*

class ScrollingActivity : AppCompatActivity() {
    override fun onBackPressed() {
        if (fabMenu.isShowing)
            fabMenu.closeMenu()
        else
            super.onBackPressed()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scrolling)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        try {
            if (fab != null && fabMenu != null) {
                val customView = View.inflate(this, R.layout.layout_custom_menu, null)
                setupCustomFilterView(customView)
                fabMenu!!.customView = customView
                fabMenu!!.bindAnchorView(fab)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


    private fun setupCustomFilterView(customView: View?) {
        if (customView != null) {
            val btnApply = customView.findViewById<Button>(R.id.btnApply)
            val cb1 = customView.findViewById<CheckBox>(R.id.cb1)
            val cb2 = customView.findViewById<CheckBox>(R.id.cb2)
            val cb3 = customView.findViewById<CheckBox>(R.id.cb3)
            val cb4 = customView.findViewById<CheckBox>(R.id.cb4)

            val filters = arrayOf(cb1, cb2, cb3, cb4)

            btnApply.setOnClickListener {
                fabMenu!!.closeMenu()
                val builder = StringBuilder("Selected:")
                for (filter in filters) {
                    if (filter.isChecked) {
                        builder.append("\n").append(filter.text.toString())
                    }
                }
                Toast.makeText(this@ScrollingActivity, builder.toString(), Toast.LENGTH_SHORT).show()
            }
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home)
            finish()
        return super.onOptionsItemSelected(item)
    }
}
