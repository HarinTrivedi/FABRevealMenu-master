package com.hlabexamples.fabrevealmenu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.hlab.fabrevealmenu.enums.Direction
import com.hlab.fabrevealmenu.listeners.OnFABMenuSelectedListener
import kotlinx.android.synthetic.main.fragment_xml.*

class DemoXmlFragment : Fragment(), OnFABMenuSelectedListener {

    private val mDirectionStrings = arrayOf("LEFT", "UP")
    private var currentDirection = Direction.LEFT

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_xml, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        try {
            if (fab != null && fabMenu != null) {
                //attach menu to fab
                fabMenu.bindAnchorView(fab)
                //set menu selection listener
                fabMenu.setOnFABMenuSelectedListener(this)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val spDirections = view.findViewById<Spinner>(R.id.spDirection)
        spDirections.adapter = ArrayAdapter(activity!!, android.R.layout.simple_spinner_dropdown_item, mDirectionStrings)
        spDirections.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, position: Int, l: Long) {
                if (fabMenu != null) {
                    if (position == 0 && currentDirection != Direction.LEFT) {
                        currentDirection = Direction.LEFT
                        fabMenu.menuDirection = Direction.LEFT
                    } else if (position == 1 && currentDirection != Direction.UP) {
                        currentDirection = Direction.UP
                        fabMenu.menuDirection = Direction.UP
                    }
                }
            }

            override fun onNothingSelected(adapterView: AdapterView<*>) {
                if (fabMenu != null) {
                    fabMenu.menuDirection = Direction.LEFT
                }
            }
        }
    }

    override fun onMenuItemSelected(view: View, id: Int) {
        when (id) {
            R.id.menu_attachment -> Toast.makeText(activity, "Attachment Selected", Toast.LENGTH_SHORT).show()
            R.id.menu_image -> Toast.makeText(activity, "Image Selected", Toast.LENGTH_SHORT).show()
            R.id.menu_place -> Toast.makeText(activity, "Place Selected", Toast.LENGTH_SHORT).show()
            R.id.menu_emoticon -> Toast.makeText(activity, "Emoticon Selected", Toast.LENGTH_SHORT).show()
        }
    }

}