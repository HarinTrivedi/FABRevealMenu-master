package com.hlab.fabrevealmenu.view

import android.content.res.ColorStateList
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hlab.fabrevealmenu.R
import com.hlab.fabrevealmenu.enums.Direction
import com.hlab.fabrevealmenu.model.FABMenuItem

class FABMenuAdapter internal constructor(
        private val parent: FABRevealMenu,
        private val mItems: List<FABMenuItem>,
        private val rowLayoutResId: Int,
        private val isCircularShape: Boolean,
        var titleTextColor: Int,
        private var titleDisabledTextColor: Int,
        var showTitle: Boolean,
        var direction: Direction,
        var animateItems: Boolean
) : RecyclerView.Adapter<FABMenuAdapter.ViewHolder>() {

    companion object {
        private const val TRANSLATE_DISTANCE = 80
        private const val ANIM_DURATION = 350
        private const val ANIM_OFFSET = 50
        private const val ANIMATED_ITEMS_COUNT = 1
    }

    private val animInterpolator = AccelerateDecelerateInterpolator()
    private var isReturning = false
    private var lastAnimatedPosition = -1
    private var maxDuration: Int = 0
    private var mMenuTitleTypeface: Typeface? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context)
                .inflate(rowLayoutResId, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setData(mItems[position])
        holder.itemView.isEnabled = mItems[position].isEnabled
        holder.tvTitle.isEnabled = mItems[position].isEnabled
        // Here you apply the animation when the view is bound
        runEnterAnimation(holder.itemView, position)
    }

    override fun getItemCount(): Int {
        return mItems.size
    }

    internal fun getItemByIndex(index: Int): FABMenuItem? {
        return if (index >= 0 && index < mItems.size) {
            mItems[index]
        } else null
    }

    internal fun getItemById(id: Int): FABMenuItem? {
        for (i in mItems.indices) {
            if (mItems[i].id == id) {
                return mItems[i]
            }
        }
        return null
    }

    internal fun notifyItemChangedById(id: Int) {
        for (i in mItems.indices) {
            if (mItems[i].id == id) {
                notifyItemChanged(i)
                return
            }
        }
    }

    private fun runEnterAnimation(view: View, position: Int) {
        if (!animateItems || position < ANIMATED_ITEMS_COUNT - 1) {
            return
        }

        if (position > lastAnimatedPosition) {
            lastAnimatedPosition = position
            if (isReturning)
                startExitAnimation(view, position)
            else
                startEnterAnimation(view, position)
        }
    }

    internal fun resetAdapter(returning: Boolean) {
        lastAnimatedPosition = -1
        isReturning = returning
        notifyDataSetChanged()
        maxDuration = itemCount * ANIM_OFFSET
    }

    private fun startEnterAnimation(view: View, position: Int) {
        val translateTo = if (direction == Direction.DOWN) -TRANSLATE_DISTANCE else TRANSLATE_DISTANCE
        view.translationY = translateTo.toFloat()
        view.alpha = 0f
        view.animate()
                .translationY(0f)
                .alpha(1f)
                .setInterpolator(animInterpolator)
                .setDuration(ANIM_DURATION.toLong())
                .setStartDelay((ANIM_OFFSET + position * ANIM_OFFSET).toLong())
                .start()
    }

    private fun startExitAnimation(view: View, position: Int) {
        val translateTo = if (direction == Direction.DOWN) -TRANSLATE_DISTANCE else TRANSLATE_DISTANCE
        view.translationY = 0f
        view.alpha = 1f
        view.animate()
                .translationY(translateTo.toFloat())
                .alpha(0f)
                .setInterpolator(animInterpolator)
                .setDuration(ANIM_DURATION.toLong())
                .setStartDelay((maxDuration - position * ANIM_OFFSET).toLong())
                .start()
    }

    internal fun setTitleDisabledTextColor(titleDisabledTextColor: Int) {
        this.titleDisabledTextColor = titleDisabledTextColor
    }

    internal fun setMenuTitleTypeface(mMenuTitleTypeface: Typeface) {
        this.mMenuTitleTypeface = mMenuTitleTypeface
    }

    inner class ViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        private var item: FABMenuItem? = null
        internal var tvTitle: TextView = itemView.findViewById(R.id.txt_title_menu_item)
        private var imgIcon: ImageView = itemView.findViewById(R.id.img_menu_item)
        private var viewParent: RelativeLayout = itemView.findViewById(R.id.view_parent)

        init {
            tvTitle.setTextColor(ColorStateList(arrayOf(intArrayOf(android.R.attr.state_enabled), intArrayOf(-android.R.attr.state_enabled)), intArrayOf(titleTextColor, titleDisabledTextColor)))
            tvTitle.visibility = if (showTitle) View.VISIBLE else View.GONE


            if (mMenuTitleTypeface != null)
                tvTitle.typeface = mMenuTitleTypeface

            viewParent.setBackgroundResource(if (isCircularShape)
                R.drawable.drawable_bg_selected_round
            else
                R.drawable.drawable_bg_selected)

            viewParent.setOnClickListener(this)
            viewParent.invalidate()
        }

        fun setData(item: FABMenuItem) {
            this.item = item
            viewParent.tag = item.id
            tvTitle.text = item.title
            imgIcon.setImageDrawable(item.iconDrawable)

        }

        override fun onClick(v: View) {
            if (item?.isEnabled == true) {
                parent.closeMenu()
                parent.menuSelectedListener?.onMenuItemSelected(v, item!!.id)
            }
        }
    }

}
