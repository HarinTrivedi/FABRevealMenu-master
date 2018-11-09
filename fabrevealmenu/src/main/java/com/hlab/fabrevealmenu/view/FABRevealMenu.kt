package com.hlab.fabrevealmenu.view

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.util.AttributeSet
import android.view.*
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.annotation.ColorRes
import androidx.annotation.MenuRes
import androidx.appcompat.view.menu.MenuBuilder
import androidx.cardview.widget.CardView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.hlab.fabrevealmenu.R
import com.hlab.fabrevealmenu.enums.Direction
import com.hlab.fabrevealmenu.helper.AnimationHelper
import com.hlab.fabrevealmenu.helper.ViewHelper
import com.hlab.fabrevealmenu.listeners.AnimationListener
import com.hlab.fabrevealmenu.listeners.OnFABMenuSelectedListener
import com.hlab.fabrevealmenu.listeners.OnMenuStateChangedListener
import com.hlab.fabrevealmenu.model.FABMenuItem
import java.util.*

class FABRevealMenu : FrameLayout {
    companion object {
        //Common constants
        private const val FAB_STATE_COLLAPSED = 0
        private const val FAB_STATE_EXPANDED = 1
        private const val FAB_MENU_SIZE_NORMAL = 0
        private const val FAB_MENU_SIZE_SMALL = 1
        private var FAB_CURRENT_STATE = FAB_STATE_COLLAPSED
    }

    var menuSelectedListener: OnFABMenuSelectedListener? = null
    private var mContext: Context? = null
    private var mCustomView: View? = null
    private var mFab: View? = null
    //attributes
    @MenuRes
    private var mMenuRes: Int = 0
    private var mMenuBackground: Int = 0
    private var mOverlayBackground: Int = 0
    private var mShowOverlay: Boolean = false
    private var mMenuSize: Int = 0
    private var mDirection: Direction? = null
    private var mShowTitle: Boolean = false
    private var mTitleTextColor: Int = 0
    private var mTitleDisabledTextColor: Int = 0
    private var animateItems: Boolean = false
    private var mMenuTitleTypeface: Typeface? = null

    //Views in the menu
    private var mOverlayLayout: FrameLayout? = null
    private var mRevealView: LinearLayout? = null
    private var mMenuView: RecyclerView? = null
    private var mEnableNestedScrolling = true
    private var mBaseView: CardView? = null
    private var menuAdapter: FABMenuAdapter? = null
    //Menu specific fields
    private var menuList: ArrayList<FABMenuItem>? = null
    //Helper class
    private var viewHelper: ViewHelper? = null
    private var animationHelper: AnimationHelper? = null
    private var menuStateChangedListener: OnMenuStateChangedListener? = null

    /**
     * Set custom view as menu
     *
     * @param view custom view
     */
    var customView: View?
        get() = mCustomView
        set(view) {
            mMenuRes = -1
            removeAllViews()
            mCustomView = view
            mCustomView?.isClickable = true
            viewHelper?.setLayoutParams(mCustomView!!)
            setUpView(mCustomView!!, false)
        }

    val isShowing: Boolean
        get() = FAB_CURRENT_STATE == FAB_STATE_EXPANDED

    var isShowOverlay: Boolean
        get() = mShowOverlay
        set(value) {
            this.mShowOverlay = value
            closeMenu()
            post { recreateView() }
        }

    private val isMenuSmall: Boolean
        get() = mMenuSize == FAB_MENU_SIZE_SMALL

    var menuDirection: Direction?
        get() = mDirection
        set(value) {
            this.mDirection = value
            if (menuAdapter != null) {
                menuAdapter!!.direction = mDirection!!
                post { recreateView() }
            }
        }

    constructor(context: Context) : super(context) {
        initView(context, null)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initView(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initView(context, attrs)
    }

    private fun initView(context: Context, attrs: AttributeSet?) {
        mContext = context

        //helper initialization
        viewHelper = ViewHelper(context)
        animationHelper = AnimationHelper(viewHelper!!)

        if (attrs != null) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.FABRevealMenu, 0, 0)

            //background
            mMenuBackground = a.getColor(R.styleable.FABRevealMenu_menuBackgroundColor, getColor(R.color.colorWhite))
            mOverlayBackground = a.getColor(R.styleable.FABRevealMenu_overlayBackground, getColor(R.color.colorOverlayDark))

            //menu
            mMenuRes = a.getResourceId(R.styleable.FABRevealMenu_menuRes, -1)

            //custom view
            val view = a.getResourceId(R.styleable.FABRevealMenu_menuCustomView, -1)
            if (view != -1)
                mCustomView = LayoutInflater.from(context).inflate(view, null)

            //direction
            mDirection = Direction.fromId(a.getInt(R.styleable.FABRevealMenu_menuDirection, 0))

            //title
            mTitleTextColor = a.getColor(R.styleable.FABRevealMenu_menuTitleTextColor, getColor(android.R.color.white))
            mTitleDisabledTextColor = a.getColor(R.styleable.FABRevealMenu_menuTitleDisabledTextColor, getColor(android.R.color.darker_gray))
            mShowTitle = a.getBoolean(R.styleable.FABRevealMenu_showTitle, true)
            mShowOverlay = a.getBoolean(R.styleable.FABRevealMenu_showOverlay, true)

            //size
            mMenuSize = a.getInt(R.styleable.FABRevealMenu_menuSize, FAB_MENU_SIZE_NORMAL)

            //animation
            animateItems = a.getBoolean(R.styleable.FABRevealMenu_animateItems, true)

            //Font
            if (a.hasValue(R.styleable.FABRevealMenu_menuTitleFontFamily)) {
                val fontId = a.getResourceId(R.styleable.FABRevealMenu_menuTitleFontFamily, -1)
                if (fontId != -1)
                    mMenuTitleTypeface = ResourcesCompat.getFont(context, fontId)
            }

            a.recycle()

            //initialization
            if (mMenuRes != -1) {
                setMenu(mMenuRes)
            } else if (mCustomView != null) {
                customView = mCustomView
            }
        }
    }

    override fun setNestedScrollingEnabled(enabled: Boolean) {
        mEnableNestedScrolling = enabled
    }

    protected fun inflateMenu(@MenuRes menuRes: Int, menu: Menu) {
        MenuInflater(context).inflate(menuRes, menu)
    }

    fun setOnMenuStateChangedListener(menuStateChangedListener: OnMenuStateChangedListener) {
        this.menuStateChangedListener = menuStateChangedListener
    }

    /**
     * Set menu from menu xml
     *
     * @param menuRes menu xml resource
     */
    fun setMenu(@MenuRes menuRes: Int) {
        mCustomView = null
        mMenuRes = menuRes
        removeAllViews()
        @SuppressLint("RestrictedApi")
        val menu = MenuBuilder(context)
        inflateMenu(menuRes, menu)
        setUpMenu(menu)
    }


    fun updateMenu() {
        mCustomView = null
        removeAllViews()
        if (menuList?.isNotEmpty() == true) {
            setUpMenuView()
        } else
            setMenu(mMenuRes)
    }

    /**
     * Set menu from list of items
     *
     * @param menuList list of items
     */
    @Throws(NullPointerException::class)
    fun setMenuItems(menuList: ArrayList<FABMenuItem>?) {
        this.menuList = menuList

        mMenuRes = -1
        mCustomView = null
        if (menuList == null)
            throw NullPointerException("Null items are not allowed.")
        removeAllViews()

        if (menuList.size > 0) {
            for (i in menuList.indices) {
                val item = menuList[i]
                item.id = i
                if (item.iconDrawable == null && item.iconBitmap != null) {
                    item.iconDrawable = BitmapDrawable(resources, item.iconBitmap)
                }
            }
        }
        setUpMenuView()
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Throws(IllegalStateException::class)
    private fun setUpMenu(menu: Menu) {
        menuList = ArrayList()
        if (menu.size() > 0) {

            for (i in 0 until menu.size()) {
                val item = menu.getItem(i)
                menuList!!.add(FABMenuItem(item.title.toString(), id = item.itemId, iconDrawable = item.icon))
            }
            setUpMenuView()
        } else
            throw IllegalStateException("Menu resource not found.")
    }

    private fun setUpMenuView() {
        if (menuList != null && menuList!!.size > 0) {
            mMenuView = viewHelper!!.generateMenuView(mEnableNestedScrolling)

            var isCircularShape = false

            //set layout manager
            if (mDirection == Direction.LEFT || mDirection == Direction.RIGHT) {
                val minItemWidth = if (isMenuSmall) mContext!!.resources.getDimension(R.dimen.column_size_small).toInt() else mContext!!.resources.getDimension(R.dimen.column_size).toInt()
                val rowLayoutResId = if (isMenuSmall) R.layout.row_horizontal_menu_item_small else R.layout.row_horizontal_menu_item

                mMenuView!!.layoutManager = DynamicGridLayoutManager(mContext!!, minItemWidth, menuList!!.size)
                menuAdapter = FABMenuAdapter(this, menuList!!, rowLayoutResId, true, mTitleTextColor, mTitleDisabledTextColor, mShowTitle, mDirection!!, animateItems)
                if (mMenuTitleTypeface != null)
                    menuAdapter!!.setMenuTitleTypeface(mMenuTitleTypeface!!)

            } else {
                isCircularShape = !mShowTitle
                val rowLayoutResId = if (isMenuSmall) R.layout.row_vertical_menu_item_small else R.layout.row_vertical_menu_item

                mMenuView!!.layoutManager = DynamicGridLayoutManager(mContext!!, 0, 0)
                menuAdapter = FABMenuAdapter(this, menuList!!, rowLayoutResId, isCircularShape, mTitleTextColor, mTitleDisabledTextColor, mShowTitle, mDirection!!, animateItems)
                if (mMenuTitleTypeface != null)
                    menuAdapter!!.setMenuTitleTypeface(mMenuTitleTypeface!!)

            }
            mMenuView!!.adapter = menuAdapter

            setUpView(mMenuView!!, mShowTitle && !isCircularShape)
        }
    }

    private fun setUpView(mView: View, toSetMinWidth: Boolean) {
        mBaseView = viewHelper!!.generateBaseView()
        mBaseView!!.setCardBackgroundColor(mMenuBackground)

        mRevealView = viewHelper!!.generateRevealView()
        mOverlayLayout = null
        mOverlayLayout = viewHelper!!.generateOverlayView()
        if (mShowOverlay) {
            mOverlayLayout!!.setBackgroundColor(if (mShowOverlay) mOverlayBackground else getColor(android.R.color.transparent))
        }

        if (toSetMinWidth)
            mBaseView!!.minimumWidth = resources.getDimensionPixelSize(if (isMenuSmall) R.dimen.menu_min_width_small else R.dimen.menu_min_width)
        //1.add menu view
        mBaseView!!.addView(mView)
        //2.add base view
        mRevealView!!.addView(mBaseView)
        //3.add overlay
        if (mOverlayLayout != null) {
            addView(mOverlayLayout)
        }
        //4.add reveal view
        addView(mRevealView)

        //set reveal center points after view is layed out
        mBaseView!!.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                mBaseView!!.viewTreeObserver.removeOnGlobalLayoutListener(this)
                animationHelper!!.calculateCenterPoints(mBaseView!!, mDirection!!)
            }
        })

        //        mRevealView.post(() -> {
        //            //set reveal center points after views are added
        //            animationHelper.calculateCenterPoints(mBaseView, mDirection);
        //        });
        if (mOverlayLayout != null) {
            mOverlayLayout!!.setOnClickListener { closeMenu() }
        }

    }

    /**
     * Attach fab to menu
     *
     * @param fab fab view
     */
    fun bindAnchorView(fab: View) {
        mFab = fab
        mFab!!.post {
            mFab!!.setOnClickListener { showMenu() }
            animationHelper!!.calculateCenterPoints(mBaseView!!, mDirection!!)
            viewHelper!!.alignMenuWithFab(mFab!!, mRevealView!!, mDirection!!)
        }
    }

    // --- action methods --- //
    fun getItemByIndex(index: Int): FABMenuItem? {
        return if (menuAdapter != null) {
            menuAdapter!!.getItemByIndex(index)
        } else null
    }

    fun getItemById(id: Int): FABMenuItem? {
        return if (menuAdapter != null) {
            menuAdapter!!.getItemById(id)
        } else null
    }

    /**
     * Remove menu item by id
     */
    fun removeItem(id: Int): Boolean {
        if (menuList != null) {
            for (i in menuList!!.indices) {
                if (menuList!![i].id == id) {
                    menuList!!.removeAt(i)
                    (mMenuView!!.layoutManager as DynamicGridLayoutManager).updateTotalItems(menuList!!.size)
                    if (menuAdapter != null) {
                        menuAdapter!!.notifyItemRemoved(i)
                        menuAdapter!!.notifyItemRangeChanged(i, menuList!!.size)
                    }
                    return true
                }
            }
        }
        return false
    }

    fun notifyItemChanged(id: Int) {
        if (menuAdapter != null) {
            menuAdapter!!.notifyItemChangedById(id)
        }
    }

    fun setOnFABMenuSelectedListener(menuSelectedListener: OnFABMenuSelectedListener) {
        this.menuSelectedListener = menuSelectedListener
    }

    /**
     * Show the menu
     */
    fun showMenu() {

        if (mFab == null) {
            throw IllegalStateException("FloatingActionButton not bound." + "Please, use bindAnchorView() to add your Fab button.")
        }

        animationHelper!!.calculateCenterPoints(mBaseView!!, mDirection!!)
        viewHelper!!.alignMenuWithFab(mFab!!, mRevealView!!, mDirection!!)

        if (FAB_CURRENT_STATE == FAB_STATE_COLLAPSED) {
            FAB_CURRENT_STATE = FAB_STATE_EXPANDED
            if (menuStateChangedListener != null) menuStateChangedListener!!.onExpand()

            animationHelper!!.moveFab(mFab!!, mRevealView!!, mDirection!!, false, object : AnimationListener() {
                override fun onEnd() {
                    mFab!!.visibility = View.INVISIBLE
                }
            })

            // Show sheet after a delay
            postDelayed({
                val finalRadius = Math.max(mBaseView!!.width, mBaseView!!.height)
                animationHelper!!.revealMenu(mBaseView!!, (mFab!!.width / 2).toFloat(), finalRadius.toFloat(), false, object : AnimationListener() {
                    override fun onStart() {
                        mRevealView!!.visibility = View.VISIBLE
                        if (menuAdapter != null) {
                            menuAdapter!!.resetAdapter(false)
                        }

                        if (mOverlayLayout != null) {
                            animationHelper!!.showOverlay(mOverlayLayout!!)
                        }
                    }
                })
            }, (AnimationHelper.FAB_ANIM_DURATION - 50).toLong())
        }

    }

    /**
     * Close the menu
     */
    @Throws(IllegalStateException::class)
    fun closeMenu() {
        if (mFab == null) {
            throw IllegalStateException("FloatingActionButton not bound." + "Please, use bindAnchorView() to add your Fab button.")
        }

        if (FAB_CURRENT_STATE == FAB_STATE_EXPANDED) {
            FAB_CURRENT_STATE = FAB_STATE_COLLAPSED
            if (menuStateChangedListener != null) menuStateChangedListener!!.onCollapse()

            val initialRadius = Math.max(mBaseView!!.width, mBaseView!!.height)
            animationHelper!!.revealMenu(mBaseView!!, initialRadius.toFloat(), (mFab!!.width / 2).toFloat(), true, object : AnimationListener() {
                override fun onStart() {
                    if (menuAdapter != null) {
                        menuAdapter!!.resetAdapter(true)
                    }
                    if (mOverlayLayout != null) {
                        animationHelper!!.hideOverlay(mOverlayLayout!!)
                    }
                }

                override fun onEnd() {
                    mRevealView!!.visibility = View.INVISIBLE
                }
            })

            // Show FAB after a delay
            postDelayed({
                animationHelper!!.moveFab(mFab!!, mRevealView!!, mDirection!!, true, object : AnimationListener() {
                    override fun onStart() {
                        mFab!!.visibility = View.VISIBLE
                    }
                })
            }, (AnimationHelper.REVEAL_DURATION - 50).toLong())
        }
    }

    private fun recreateView() {
        when {
            mMenuRes != -1 -> updateMenu()
            mCustomView != null -> customView = mCustomView
            menuList != null -> setMenuItems(menuList)
        }
    }

    private fun getColor(colorResId: Int): Int {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            resources.getColor(colorResId, mContext!!.theme)
        else
            resources.getColor(colorResId)
        return colorResId
    }

    // ---- getter setter --- //

    @Throws(NullPointerException::class)
    fun setOverlayBackground(@ColorRes mOverlayBackground: Int) {
        this.mOverlayBackground = mOverlayBackground
        if (mOverlayLayout != null) {
            mOverlayLayout!!.setBackgroundColor(getColor(mOverlayBackground))
        } else
            throw NullPointerException("Overlay view is not initialized/ set ShowOverlay to true")
    }

    fun setMenuBackground(@ColorRes menuBackgroundRes: Int) {
        mBaseView!!.setCardBackgroundColor(getColor(menuBackgroundRes))
    }

    /**
     * Set normal size for menu item
     */
    fun enableItemAnimation(enabled: Boolean) {
        animateItems = enabled
        if (menuAdapter != null) {
            post {
                menuAdapter!!.animateItems = enabled
                menuAdapter!!.notifyDataSetChanged()
            }
        }
    }

    /**
     * Set small size for menu item
     */
    fun setSmallerMenu() {
        mMenuSize = FAB_MENU_SIZE_SMALL
        post { recreateView() }
    }

    /**
     * Set normal size for menu item
     */
    fun setNormalMenu() {
        mMenuSize = FAB_MENU_SIZE_NORMAL
        post { recreateView() }
    }

    fun setTitleVisible(mShowTitle: Boolean) {
        this.mShowTitle = mShowTitle
        if (menuAdapter != null) {
            if (mShowTitle && (mDirection == Direction.UP || mDirection == Direction.DOWN))
                mBaseView!!.minimumWidth = resources.getDimensionPixelSize(R.dimen.menu_min_width)
            else
                mBaseView!!.minimumWidth = FrameLayout.LayoutParams.WRAP_CONTENT
            menuAdapter!!.showTitle = mShowTitle
            closeMenu()
            post { recreateView() }
        }
    }

    fun setMenuTitleTextColor(@ColorRes mTitleTextColor: Int) {
        this.mTitleTextColor = mTitleTextColor
        if (menuAdapter != null) {
            menuAdapter!!.titleTextColor = mTitleTextColor
            menuAdapter!!.notifyDataSetChanged()
        }
    }

    fun setMenuTitleDisabledTextColor(@ColorRes mTitleDisabledTextColor: Int) {
        this.mTitleDisabledTextColor = mTitleDisabledTextColor
        if (menuAdapter != null) {
            menuAdapter!!.setTitleDisabledTextColor(mTitleDisabledTextColor)
            menuAdapter!!.notifyDataSetChanged()
        }
    }

    fun setMenuTitleTypeface(mMenuTitleTypeface: Typeface?) {
        if (mMenuTitleTypeface != null) {
            this.mMenuTitleTypeface = mMenuTitleTypeface
            post { recreateView() }
        }
    }
}
