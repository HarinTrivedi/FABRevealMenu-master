package com.hlab.fabrevealmenu.view;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.ColorRes;
import androidx.annotation.MenuRes;
import androidx.annotation.NonNull;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.hlab.fabrevealmenu.R;
import com.hlab.fabrevealmenu.helper.AnimationHelper;
import com.hlab.fabrevealmenu.helper.RevealDirection;
import com.hlab.fabrevealmenu.helper.OnFABMenuSelectedListener;
import com.hlab.fabrevealmenu.helper.ViewHelper;
import com.hlab.fabrevealmenu.model.FABMenuItem;

import java.util.ArrayList;

public class FABRevealMenu extends FrameLayout {

    //Common constants
    private final int FAB_STATE_COLLAPSED = 0;
    private final int FAB_STATE_EXPANDED = 1;
    private final int FAB_MENU_SIZE_NORMAL = 0;
    private final int FAB_MENU_SIZE_SMALL = 1;

    public OnFABMenuSelectedListener menuSelectedListener = null;
    private Context mContext;
    private View mCustomView;
    private View mFab;
    //attributes
    @MenuRes
    private int mMenuRes;
    private int mMenuBackground;
    private int mOverlayBackground;
    private boolean mShowOverlay;
    private int mMenuSize;
    private RevealDirection mRevealDirection;
    private boolean mShowTitle;
    private boolean mShowIcon;
    private int mTitleTextColor;
    private int mTitleDisabledTextColor;
    private int FAB_CURRENT_STATE = FAB_STATE_COLLAPSED;
    private Typeface mMenuTitleTypeface;
    private int mMenuCornerRadius;
    private int mDuration;

    //Views in the menu
    private FrameLayout mOverlayLayout = null;
    private LinearLayout mRevealView = null;
    private RecyclerView mMenuView = null;
    private boolean mEnableNestedScrolling = true;
    private CardView mBaseView = null;
    private FABMenuAdapter menuAdapter = null;
    //Menu specific fields
    private ArrayList<FABMenuItem> menuList = null;
    //Helper class
    private ViewHelper viewHelper;
    private AnimationHelper animationHelper;

    public FABRevealMenu(Context context) {
        super(context);
        initView(context, null);
    }

    public FABRevealMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    public FABRevealMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        mContext = context;

        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.FABRevealMenu, 0, 0);

            //background
            mMenuBackground = a.getColor(R.styleable.FABRevealMenu_menuBackgroundColor, getColor(R.color.colorWhite));
            mOverlayBackground = a.getColor(R.styleable.FABRevealMenu_overlayBackground, getColor(R.color.colorOverlayDark));

            //menu
            mMenuRes = a.getResourceId(R.styleable.FABRevealMenu_menuRes, -1);

            //custom view
            int customView = a.getResourceId(R.styleable.FABRevealMenu_menuCustomView, -1);
            if (customView != -1)
                mCustomView = LayoutInflater.from(context).inflate(customView, null);

            //direction
            mRevealDirection = RevealDirection.fromId(a.getInt(R.styleable.FABRevealMenu_menuDirection, 0));

            //title
            mTitleTextColor = a.getColor(R.styleable.FABRevealMenu_menuTitleTextColor, getColor(android.R.color.white));
            mTitleDisabledTextColor = a.getColor(R.styleable.FABRevealMenu_menuTitleDisabledTextColor, getColor(android.R.color.darker_gray));
            mShowTitle = a.getBoolean(R.styleable.FABRevealMenu_showTitle, true);
            mShowIcon = a.getBoolean(R.styleable.FABRevealMenu_showIcon, true);
            mShowOverlay = a.getBoolean(R.styleable.FABRevealMenu_showOverlay, true);

            //size
            mMenuSize = a.getInt(R.styleable.FABRevealMenu_menuSize, FAB_MENU_SIZE_NORMAL);
            mMenuCornerRadius = a.getDimensionPixelSize(R.styleable.FABRevealMenu_menuCornerRadius, 10);
            mDuration = a.getInteger(R.styleable.FABRevealMenu_duration, 500);

            //Font
            if (a.hasValue(R.styleable.FABRevealMenu_menuTitleFontFamily)) {
                int fontId = a.getResourceId(R.styleable.FABRevealMenu_menuTitleFontFamily, -1);
                if (fontId != -1)
                    mMenuTitleTypeface = ResourcesCompat.getFont(context, fontId);
            }

            a.recycle();

            //helper initialization
            viewHelper = new ViewHelper(context);
            animationHelper = new AnimationHelper(mDuration);

            //initialization
            if (mMenuRes != -1) {
                setMenu(mMenuRes);
            } else if (mCustomView != null) {
                setCustomView(mCustomView);
            }
        }
    }

    public View getCustomView() {
        return mCustomView;
    }

    /**
     * Set custom view as menu
     *
     * @param view custom view
     */
    public void setCustomView(@NonNull View view) {
        mMenuRes = -1;
        removeAllViews();
        mCustomView = view;
        mCustomView.setClickable(true);
        viewHelper.setLayoutParams(mCustomView);
        setUpView(mCustomView, false);
    }

    public void setNestedScrollingEnabled(boolean enabled) {
        mEnableNestedScrolling = enabled;
    }

    /**
     * Set menu from menu xml
     *
     * @param menuRes menu xml resource
     */
    public void setMenu(@MenuRes int menuRes) {
        mCustomView = null;
        mMenuRes = menuRes;
        removeAllViews();
        @SuppressLint("RestrictedApi")
        Menu menu = new MenuBuilder(getContext());
        inflateMenu(menuRes, menu);
        setUpMenu(menu);
    }

    protected void inflateMenu(@MenuRes int menuRes, Menu menu) {
        new MenuInflater(getContext()).inflate(menuRes, menu);
    }

    public void updateMenu() {
        mCustomView = null;
        removeAllViews();
        if (menuList.size() > 0) {
            setUpMenuView();
        } else
            setMenu(mMenuRes);
    }

    /**
     * Set menu from list of items
     *
     * @param menuList list of items
     */
    public void setMenuItems(ArrayList<FABMenuItem> menuList) throws NullPointerException {
        this.menuList = menuList;

        mMenuRes = -1;
        mCustomView = null;
        if (menuList == null)
            throw new NullPointerException("Null items are not allowed.");
        removeAllViews();

        if (menuList.size() > 0) {
            for (int i = 0; i < menuList.size(); i++) {
                FABMenuItem item = menuList.get(i);
                item.setId(i);
                if (item.getIconDrawable() == null && item.getIconBitmap() != null) {
                    item.setIconDrawable(new BitmapDrawable(getResources(), item.getIconBitmap()));
                }
            }
        }
        setUpMenuView();
    }

    private void setUpMenu(@NonNull Menu menu) throws IllegalStateException {
        menuList = new ArrayList<>();
        if (menu.size() > 0) {

            for (int i = 0; i < menu.size(); i++) {
                MenuItem item = menu.getItem(i);
                menuList.add(new FABMenuItem(item.getItemId(), item.getTitle().toString(), item.getIcon()));
            }
            setUpMenuView();
        } else
            throw new IllegalStateException("Menu resource not found.");
    }

    private void setUpMenuView() {
        if (menuList != null && menuList.size() > 0) {
            mMenuView = viewHelper.generateMenuView(mEnableNestedScrolling);

            boolean isCircularShape = false;

            //set layout manager
            if (mRevealDirection == RevealDirection.LEFT || mRevealDirection == RevealDirection.RIGHT) {
                int minItemWidth = isMenuSmall() ? (int) mContext.getResources().getDimension(R.dimen.column_size_small) : (int) mContext.getResources().getDimension(R.dimen.column_size);
                int rowLayoutResId = isMenuSmall() ? R.layout.row_horizontal_menu_item_small : R.layout.row_horizontal_menu_item;

                mMenuView.setLayoutManager(new DynamicGridLayoutManager(mContext, minItemWidth, menuList.size()));
                menuAdapter = new FABMenuAdapter(this, menuList, rowLayoutResId, true, mTitleTextColor, mTitleDisabledTextColor, mShowTitle, mShowIcon, mRevealDirection);
                if (mMenuTitleTypeface != null)
                    menuAdapter.setMenuTitleTypeface(mMenuTitleTypeface);

            } else {
                isCircularShape = !mShowTitle;
                int rowLayoutResId = isMenuSmall() ? R.layout.row_vertical_menu_item_small : R.layout.row_vertical_menu_item;

                mMenuView.setLayoutManager(new DynamicGridLayoutManager(mContext, 0, 0));
                menuAdapter = new FABMenuAdapter(this, menuList, rowLayoutResId, isCircularShape, mTitleTextColor, mTitleDisabledTextColor, mShowTitle, mShowIcon, mRevealDirection);
                if (mMenuTitleTypeface != null)
                    menuAdapter.setMenuTitleTypeface(mMenuTitleTypeface);

            }
            mMenuView.setAdapter(menuAdapter);

            setUpView(mMenuView, mShowTitle && !isCircularShape);
        }
    }

    private void setUpView(View mView, boolean toSetMinWidth) {
        mBaseView = viewHelper.generateBaseView(mMenuCornerRadius);
        mBaseView.setCardBackgroundColor(mMenuBackground);

        mRevealView = viewHelper.generateRevealView();
        mOverlayLayout = null;
        mOverlayLayout = viewHelper.generateOverlayView();
        if (mShowOverlay) {
            mOverlayLayout.setBackgroundColor(mShowOverlay ? mOverlayBackground : getColor(android.R.color.transparent));
        }

        if (toSetMinWidth)
            mBaseView.setMinimumWidth(getResources().getDimensionPixelSize(isMenuSmall() ? R.dimen.menu_min_width_small : R.dimen.menu_min_width));
        //1.add menu view
        mBaseView.addView(mView);
        //2.add base view
        mRevealView.addView(mBaseView);
        //3.add overlay
        if (mOverlayLayout != null) {
            addView(mOverlayLayout);
        }
        //4.add reveal view
        addView(mRevealView);

        if (mOverlayLayout != null) {
            mOverlayLayout.setOnClickListener(v -> closeMenu());
        }

    }

    /**
     * Attach fab to menu
     *
     * @param fab fab view
     */
    public void bindAnchorView(@NonNull View fab) {
        mFab = fab;
        mFab.post(() -> {
            ViewCompat.setTransitionName(mFab, "FAB");
            mFab.setOnClickListener(v -> showMenu());
            viewHelper.alignMenuWithFab(mFab, mRevealView, mRevealDirection);
        });
    }

    // --- action methods --- //

    public FABMenuItem getItemByIndex(int index) {
        if (menuAdapter != null) {
            return menuAdapter.getItemByIndex(index);
        }
        return null;
    }

    public FABMenuItem getItemById(int id) {
        if (menuAdapter != null) {
            return menuAdapter.getItemById(id);
        }
        return null;
    }

    /**
     * Remove menu item by id
     */
    public boolean removeItem(int id) {
        if (menuList != null) {
            for (int i = 0; i < menuList.size(); i++) {
                if (menuList.get(i).getId() == id) {
                    menuList.remove(i);
                    ((DynamicGridLayoutManager) mMenuView.getLayoutManager()).updateTotalItems(menuList.size());
                    if (menuAdapter != null) {
                        menuAdapter.notifyItemRemoved(i);
                        menuAdapter.notifyItemRangeChanged(i, menuList.size());
                    }
                    return true;
                }
            }
        }
        return false;
    }

    public void notifyItemChanged(int id) {
        if (menuAdapter != null) {
            menuAdapter.notifyItemChangedById(id);
        }
    }

    public void setOnFABMenuSelectedListener(OnFABMenuSelectedListener menuSelectedListener) {
        this.menuSelectedListener = menuSelectedListener;
    }

    public boolean isShowing() {
        return (FAB_CURRENT_STATE == FAB_STATE_EXPANDED);
    }

    /**
     * Show the menu
     */
    public void showMenu() {

        if (mFab == null) {
            throw new IllegalStateException("FloatingActionButton not bound." +
                    "Please, use bindAnchorView() to add your Fab button.");
        }
        if (FAB_CURRENT_STATE == FAB_STATE_COLLAPSED) {
            FAB_CURRENT_STATE = FAB_STATE_EXPANDED;
            viewHelper.alignMenuWithFab(mFab, mRevealView, mRevealDirection);
            animationHelper.revealMenu(this, mFab, mRevealView, false);
            if (mShowOverlay)
                animationHelper.showOverlay(mOverlayLayout);
        }
    }

    /**
     * Close the menu
     */
    public void closeMenu() throws IllegalStateException {
        if (mFab == null) {
            throw new IllegalStateException("FloatingActionButton not bound." +
                    "Please, use bindAnchorView() to add your Fab button.");
        }

        if (FAB_CURRENT_STATE == FAB_STATE_EXPANDED) {
            FAB_CURRENT_STATE = FAB_STATE_COLLAPSED;
            viewHelper.alignMenuWithFab(mFab, mRevealView, mRevealDirection);
            animationHelper.revealMenu(this, mFab, mRevealView, true);
            if (mShowOverlay)
                animationHelper.hideOverlay(mOverlayLayout);
        }
    }

    private void recreateView() {
        if (mMenuRes != -1)
            updateMenu();
        else if (mCustomView != null)
            setCustomView(mCustomView);
        else if (menuList != null)
            setMenuItems(menuList);
    }

    private int getColor(int colorResId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            getResources().getColor(colorResId, mContext.getTheme());
        else
            getResources().getColor(colorResId);
        return colorResId;
    }

    // ---- getter setter --- //

    public void setOverlayBackground(@ColorRes int mOverlayBackground) throws NullPointerException {
        this.mOverlayBackground = mOverlayBackground;
        if (mOverlayLayout != null) {
            mOverlayLayout.setBackgroundColor(getColor(mOverlayBackground));
        } else
            throw new NullPointerException("Overlay view is not initialized/ set ShowOverlay to true");
    }

    public void setMenuBackground(@ColorRes int menuBackgroundRes) {
        mBaseView.setCardBackgroundColor(getColor(menuBackgroundRes));
    }

    public boolean isShowOverlay() {
        return mShowOverlay;
    }

    public void setShowOverlay(boolean mShowOverlay) {
        this.mShowOverlay = mShowOverlay;
        closeMenu();
        post(this::recreateView);
    }

    private boolean isMenuSmall() {
        return mMenuSize == FAB_MENU_SIZE_SMALL;
    }

    /**
     * Set small size for menu item
     */
    public void setSmallerMenu() {
        mMenuSize = FAB_MENU_SIZE_SMALL;
        post(this::recreateView);
    }

    /**
     * Set normal size for menu item
     */
    public void setNormalMenu() {
        mMenuSize = FAB_MENU_SIZE_NORMAL;
        post(this::recreateView);
    }

    public void setTitleVisible(boolean mShowTitle) {
        this.mShowTitle = mShowTitle;
        if (menuAdapter != null) {
            if (mShowTitle && (mRevealDirection == RevealDirection.UP || mRevealDirection == RevealDirection.DOWN))
                mBaseView.setMinimumWidth(getResources().getDimensionPixelSize(R.dimen.menu_min_width));
            else
                mBaseView.setMinimumWidth(LayoutParams.WRAP_CONTENT);
            menuAdapter.setShowTitle(mShowTitle);
            closeMenu();
            post(this::recreateView);
        }
    }

    public void setMenuTitleTextColor(@ColorRes int mTitleTextColor) {
        this.mTitleTextColor = mTitleTextColor;
        if (menuAdapter != null) {
            menuAdapter.setTitleTextColor(mTitleTextColor);
            menuAdapter.notifyDataSetChanged();
        }
    }

    public void setMenuTitleDisabledTextColor(@ColorRes int mTitleDisabledTextColor) {
        this.mTitleDisabledTextColor = mTitleDisabledTextColor;
        if (menuAdapter != null) {
            menuAdapter.setTitleDisabledTextColor(mTitleDisabledTextColor);
            menuAdapter.notifyDataSetChanged();
        }
    }

    public RevealDirection getMenuDirection() {
        return mRevealDirection;
    }

    public void setMenuDirection(RevealDirection mRevealDirection) {
        this.mRevealDirection = mRevealDirection;
        if (menuAdapter != null) {
            menuAdapter.setDirection(mRevealDirection);
            post(this::recreateView);
        }
    }

    public void setMenuTitleTypeface(Typeface mMenuTitleTypeface) {
        if (mMenuTitleTypeface != null) {
            this.mMenuTitleTypeface = mMenuTitleTypeface;
            post(this::recreateView);
        }
    }
}
