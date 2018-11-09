package com.hlabexamples.fabrevealmenu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.hlab.fabrevealmenu.enums.Direction
import com.hlab.fabrevealmenu.listeners.OnFABMenuSelectedListener
import com.hlab.fabrevealmenu.model.FABMenuItem
import kotlinx.android.synthetic.main.fragment_code.*

class DemoCodeFragment : Fragment(), OnFABMenuSelectedListener {

    private var items = arrayListOf<FABMenuItem>()
    private val mDirectionStrings = arrayOf("LEFT", "UP")
    private var currentDirection = Direction.LEFT

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_code, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initItems(false)

        try {
            if (fab != null && fabMenu != null) {
                //attach menu to fab
                //set menu items from arrylist
                fabMenu.setMenuItems(items)
                //attach menu to fab
                fabMenu.bindAnchorView(fab)
                //set menu item selection
                fabMenu.setOnFABMenuSelectedListener(this)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val cbTitle = view.findViewById<CheckBox>(R.id.chTitle)
        cbTitle.setOnCheckedChangeListener { _, isChecked ->
            fabMenu?.setTitleVisible(isChecked)
        }
        val cbShowOverlay = view.findViewById<CheckBox>(R.id.chOverlay)
        cbShowOverlay.setOnCheckedChangeListener { _, isChecked ->
            if (fabMenu != null) {
                fabMenu.isShowOverlay = isChecked
            }
        }
        val cbDouble = view.findViewById<CheckBox>(R.id.chDouble)
        cbDouble.setOnCheckedChangeListener { buttonView, isChecked ->
            if (fabMenu != null) {
                initItems(isChecked)
                fabMenu.setMenuItems(items)
            }
        }
        val cbFont = view.findViewById<CheckBox>(R.id.chFont)
        cbFont.setOnCheckedChangeListener { buttonView, isChecked ->
            fabMenu?.setMenuTitleTypeface(ResourcesCompat.getFont(activity!!, R.font.quicksand))
        }
        val chSmall = view.findViewById<CheckBox>(R.id.chSmall)
        chSmall.setOnCheckedChangeListener { buttonView, isChecked ->
            if (fabMenu != null) {
                if (isChecked)
                    fabMenu.setSmallerMenu()
                else
                    fabMenu.setNormalMenu()
            }
        }
        val chAnimate = view.findViewById<CheckBox>(R.id.chAnimate)
        chAnimate.setOnCheckedChangeListener { _, isChecked ->
            fabMenu?.enableItemAnimation(isChecked)
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

        fabMenu.setOverlayBackground(R.color.colorAccent)
        fabMenu.setMenuBackground(R.color.colorWhite)
    }

    private fun initItems(toShowDoubleItems: Boolean) {
        items.clear()
        items.add(FABMenuItem("Attachments", iconDrawable = AppCompatResources.getDrawable(activity!!, R.drawable.ic_attachment)))
        items.add(FABMenuItem("Images", iconDrawable = AppCompatResources.getDrawable(activity!!, R.drawable.ic_image)))
        items.add(FABMenuItem("Places", iconDrawable = AppCompatResources.getDrawable(activity!!, R.drawable.ic_place)))
        items.add(FABMenuItem("Emoticons", iconDrawable = AppCompatResources.getDrawable(activity!!, R.drawable.ic_emoticon)))
        if (toShowDoubleItems) {
            items.add(FABMenuItem("Attachments", iconDrawable = AppCompatResources.getDrawable(activity!!, R.drawable.ic_attachment)))
            items.add(FABMenuItem("Images", iconDrawable = AppCompatResources.getDrawable(activity!!, R.drawable.ic_image)))
            items.add(FABMenuItem("Places", iconDrawable = AppCompatResources.getDrawable(activity!!, R.drawable.ic_place)))
            items.add(FABMenuItem("Emoticons", iconDrawable = AppCompatResources.getDrawable(activity!!, R.drawable.ic_emoticon)))
        }
    }

    override fun onMenuItemSelected(view: View, id: Int) {
        if (id >= 0 && items.size > id) {
            Toast.makeText(activity, items[id].title + "Clicked", Toast.LENGTH_SHORT).show()
        }
    }
}