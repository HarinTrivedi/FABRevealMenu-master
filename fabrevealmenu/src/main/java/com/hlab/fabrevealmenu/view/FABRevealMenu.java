package com.hlab.fabrevealmenu.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.BoolRes;
import android.support.annotation.ColorRes;
import android.support.annotation.MenuRes;
import android.support.annotation.NonNull;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.hlab.fabrevealmenu.R;
import com.hlab.fabrevealmenu.enums.Direction;
import com.hlab.fabrevealmenu.helper.AnimationHelper;
import com.hlab.fabrevealmenu.helper.ViewHelper;
import com.hlab.fabrevealmenu.listeners.AnimationListener;
import com.hlab.fabrevealmenu.listeners.OnFABMenuSelectedListener;
import com.hlab.fabrevealmenu.model.FABMenuItem;

import java.util.ArrayList;

import io.codetail.widget.RevealLinearLayout;

public class FABRevealMenu extends FrameLayout {

    private Context mContext;
    private View mCustomView;
    private View mFab;

    //attributes
    @MenuRes
    private int mMenuRes;
    private int mMenuBackground;
    private int mOverlayBackground;
    private boolean mShowOverlay;

    private Direction mDirection;
    @BoolRes
    private boolean mShowTitle;
    private int mTitleTextColor;

    private boolean animateItems;
    //Common constants
    private final int FAB_STATE_COLLAPSED = 0;
    private final int FAB_STATE_EXPANDED = 1;
    private int FAB_CURRENT_STATE = FAB_STATE_COLLAPSED;


    //Views in the menu
    private FrameLayout mOverlayLayout = null;
    private RevealLinearLayout mRevealView = null;
    private RecyclerView mMenuView = null;
    private CardView mBaseView = null;
    private FABMenuAdapter menuAdapter = null;

    //Menu specific fields
    private ArrayList<FABMenuItem> menuList = null;
    public OnFABMenuSelectedListener menuSelectedListener = null;

    //Helper class
    private ViewHelper viewHelper;
    private AnimationHelper animationHelper;

    private final int CONST_DELAY = 700;

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

        //helper initialization
        viewHelper = new ViewHelper(context);
        animationHelper = new AnimationHelper(viewHelper);

        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.FABRevealMenu, 0, 0);

            //background
            mMenuBackground = a.getColor(R.styleable.FABRevealMenu_menuBackgroundColor, getColor(R.color.colorWhite));
            mOverlayBackground = a.getColor(R.styleable.FABRevealMenu_overlayBackground, getColor(R.color.colorOverlayDark));

            //menu
            mMenuRes = a.getResourceId(R.styleable.FABRevealMenu_menuRes, -1);

            //custom view
            int customView = a.getResourceId(R.styleable.FABRevealMenu_menuCustomView, -1);
            if (customView != -1) {
                mCustomView = LayoutInflater.from(context).inflate(customView, this, true);
            }

            //direction
            mDirection = Direction.fromId(a.getInt(R.styleable.FABRevealMenu_menuDirection, 0));

            //title
            mTitleTextColor = a.getColor(R.styleable.FABRevealMenu_menuTitleTextColor, getColor(android.R.color.white));
            mShowTitle = a.getBoolean(R.styleable.FABRevealMenu_showTitle, true);
            mShowOverlay = a.getBoolean(R.styleable.FABRevealMenu_showOverlay, true);

            //animation
            animateItems = a.getBoolean(R.styleable.FABRevealMenu_animateItems, true);

            a.recycle();

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

    public void setCustomView(@NonNull View view) {
        mMenuRes = -1;
        removeAllViews();
        mCustomView = view;
        mCustomView.setClickable(true);
        viewHelper.setLayoutParams(mCustomView);
        setUpView(mCustomView, false);
    }

    public void setMenu(@MenuRes int menuRes) {
        mCustomView = null;
        mMenuRes = menuRes;
        removeAllViews();
        Menu menu = new MenuBuilder(getContext());
        new MenuInflater(getContext()).inflate(menuRes, menu);
        setUpMenu(menu);
    }

    public void setMenuItems(ArrayList<FABMenuItem> menuList) throws NullPointerException {
        this.menuList = menuList;

        mMenuRes = -1;
        mCustomView = null;
        animateItems = false;
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


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
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
            mMenuView = viewHelper.generateMenuView();

            boolean isCircularShape = false;
            //set layout manager
            if (mDirection == Direction.LEFT || mDirection == Direction.RIGHT) {
                mMenuView.setLayoutManager(new DynamicGridLayoutManager(mContext, (int) mContext.getResources().getDimension(R.dimen.column_size), menuList.size()));
                menuAdapter = new FABMenuAdapter(this, menuList, R.layout.row_horizontal_menu_item, true, mTitleTextColor, mShowTitle, mDirection, animateItems);
            } else {
                isCircularShape = !mShowTitle;
                mMenuView.setLayoutManager(new DynamicGridLayoutManager(mContext, 0, 0));
                menuAdapter = new FABMenuAdapter(this, menuList, R.layout.row_vertical_menu_item, isCircularShape, mTitleTextColor, mShowTitle, mDirection, animateItems);
            }
            mMenuView.setAdapter(menuAdapter);

            setUpView(mMenuView, mShowTitle && !isCircularShape);
        }
    }

    private void setUpView(View mView, boolean toSetMinWidth) {
        mBaseView = viewHelper.generateBaseView();
        mRevealView = viewHelper.generateRevealView();
        mOverlayLayout = null;
        if (mShowOverlay) {
            mOverlayLayout = viewHelper.generateOverlayView();
            mOverlayLayout.setBackgroundColor(mOverlayBackground);
        }
        mBaseView.setCardBackgroundColor(mMenuBackground);


        if (toSetMinWidth)
            mBaseView.setMinimumWidth(getResources().getDimensionPixelSize(R.dimen.menu_min_width));
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

        mRevealView.post(new Runnable() {
            @Override
            public void run() {
                //set reveal center points after views are added
                animationHelper.calculateCenterPoints(mBaseView, mDirection);
            }
        });
        if (mOverlayLayout != null) {
            mOverlayLayout.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    closeMenu();
                }
            });
        }

    }

    public void bindAncherView(@NonNull View fab) {
        mFab = fab;
        mFab.post(new Runnable() {
            @Override
            public void run() {
                mFab.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showMenu();
                    }
                });
                animationHelper.calculateCenterPoints(mBaseView, mDirection);
                viewHelper.alignMenuWithFab(mFab, mRevealView, mDirection);
            }
        });
    }

    // --- action methods --- //

    public void setOnFABMenuSelectedListener(OnFABMenuSelectedListener menuSelectedListener) {
        this.menuSelectedListener = menuSelectedListener;
    }

    public boolean isShowing() {
        return (FAB_CURRENT_STATE == FAB_STATE_EXPANDED);
    }

    public void showMenu() {

        if (mFab == null) {
            throw new IllegalStateException("FloatingActionButton not bound." +
                    "Please, use bindAncherView() to add your Fab button.");
        }

        animationHelper.calculateCenterPoints(mBaseView, mDirection);
        viewHelper.alignMenuWithFab(mFab, mRevealView, mDirection);

        if (FAB_CURRENT_STATE == FAB_STATE_COLLAPSED) {
            FAB_CURRENT_STATE = FAB_STATE_EXPANDED;

            animationHelper.moveFab(mFab, mRevealView, mDirection, false, new AnimationListener() {
                @Override
                public void onEnd() {
                    mFab.setVisibility(View.INVISIBLE);
                }
            });

            // Show sheet after a delay
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    int finalRadius = Math.max(mBaseView.getWidth(), mBaseView.getHeight());
                    animationHelper.revealMenu(mBaseView, mFab.getWidth() / 2, finalRadius, false, new AnimationListener() {
                        @Override
                        public void onStart() {
                            mRevealView.setVisibility(View.VISIBLE);
                            if (menuAdapter != null) {
                                menuAdapter.resetAdapter(false);
                            }

                            if (mOverlayLayout != null) {
                                animationHelper.showOverlay(mOverlayLayout);
                            }
                        }
                    });
                }
            }, AnimationHelper.FAB_ANIM_DURATION - 50);
        }

    }

    public void closeMenu() throws IllegalStateException {
        if (mFab == null) {
            throw new IllegalStateException("FloatingActionButton not bound." +
                    "Please, use bindAncherView() to add your Fab button.");
        }

        if (FAB_CURRENT_STATE == FAB_STATE_EXPANDED) {
            FAB_CURRENT_STATE = FAB_STATE_COLLAPSED;

            int initialRadius = Math.max(mBaseView.getWidth(), mBaseView.getHeight());
            animationHelper.revealMenu(mBaseView, initialRadius, mFab.getWidth() / 2, true, new AnimationListener() {
                @Override
                public void onStart() {
                    if (menuAdapter != null) {
                        menuAdapter.resetAdapter(true);
                    }
                    if (mOverlayLayout != null) {
                        animationHelper.hideOverlay(mOverlayLayout);
                    }
                }

                @Override
                public void onEnd() {
                    mRevealView.setVisibility(View.INVISIBLE);
                }
            });

            // Show FAB after a delay
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    animationHelper.moveFab(mFab, mRevealView, mDirection, true, new AnimationListener() {
                        @Override
                        public void onStart() {
                            mFab.setVisibility(View.VISIBLE);
                        }
                    });
                }
            }, AnimationHelper.REVEAL_DURATION - 50);
        }
    }

    private void recreateView() {
        if (mMenuRes != -1)
            setMenu(mMenuRes);
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
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                recreateView();
            }
        }, CONST_DELAY);

    }

    public void setTitleVisible(boolean mShowTitle) {
        this.mShowTitle = mShowTitle;
        if (menuAdapter != null) {
            if (mShowTitle && (mDirection == Direction.UP || mDirection == Direction.DOWN))
                mBaseView.setMinimumWidth(getResources().getDimensionPixelSize(R.dimen.menu_min_width));
            else
                mBaseView.setMinimumWidth(LayoutParams.WRAP_CONTENT);
            menuAdapter.setShowTitle(mShowTitle);
            closeMenu();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    recreateView();
                }
            }, CONST_DELAY);
        }
    }

    public void setMenuTitleTextColor(@ColorRes int mTitleTextColor) {
        this.mTitleTextColor = mTitleTextColor;
        if (menuAdapter != null) {
            menuAdapter.setTitleTextColor(mTitleTextColor);
        }
    }

    public Direction getMenuDirection() {
        return mDirection;
    }

    public void setMenuDirection(Direction mDirection) {
        this.mDirection = mDirection;
        if (menuAdapter != null) {
            menuAdapter.setDirection(mDirection);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    recreateView();
                }
            }, CONST_DELAY);
        }
    }
}
