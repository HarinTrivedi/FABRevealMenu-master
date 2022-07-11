package com.hlab.fabrevealmenu.helper;

import android.content.Context;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

public class ViewHelper {

    private final int SHEET_REVEAL_OFFSET_Y = 5;
    private Context mContext;
    //common layout parameter
    private ViewGroup.LayoutParams matchParams = null;
    private ViewGroup.LayoutParams wrapParams = null;

    public ViewHelper(Context context) {
        mContext = context;
        matchParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        wrapParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    public CardView generateBaseView(int radius) {
        //Base view
        MaterialCardView mBaseView = new MaterialCardView(mContext);
        mBaseView.setRadius(radius);
        mBaseView.setLayoutParams(matchParams);
        return mBaseView;
    }

    public RecyclerView generateMenuView(boolean enableNestedScrolling) {
        //Create menu view
        RecyclerView mMenuView = new RecyclerView(mContext);
        mMenuView.setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);
        mMenuView.setBackgroundColor(Color.TRANSPARENT);
        mMenuView.setLayoutParams(matchParams);
        final int padding = dpToPx(mContext, 10);
        mMenuView.setPadding(padding, padding, padding, padding);
        mMenuView.setNestedScrollingEnabled(enableNestedScrolling);
        return mMenuView;
    }

    public LinearLayout generateRevealView() {
        //Reveal view
        LinearLayout mRevealView = new LinearLayout(mContext);
        mRevealView.setBackgroundColor(Color.TRANSPARENT);
        mRevealView.setLayoutParams(wrapParams);
        mRevealView.setVisibility(View.INVISIBLE);
        return mRevealView;
    }

    public FrameLayout generateOverlayView() {
        //Overlay view
        FrameLayout mOverlayLayout = new FrameLayout(mContext);
        mOverlayLayout.setLayoutParams(matchParams);
        mOverlayLayout.animate().alpha(0);
        mOverlayLayout.setVisibility(View.GONE);
        mOverlayLayout.setClickable(true);
        return mOverlayLayout;
    }

    public void setLayoutParams(View mView) {
        mView.setLayoutParams(matchParams);
    }

    /**
     * Aligns the sheet's position with the FAB.
     */
    public void alignMenuWithFab(View mFab, View mRevealView, RevealDirection mRevealDirection) {
        ViewGroup.MarginLayoutParams sheetLayoutParams = (ViewGroup.MarginLayoutParams)
                mRevealView.getLayoutParams();
        ViewGroup.MarginLayoutParams fabLayoutParams = (ViewGroup.MarginLayoutParams)
                mFab.getLayoutParams();

        //adjust sheet margin
        sheetLayoutParams.setMargins(fabLayoutParams.leftMargin, fabLayoutParams.topMargin, fabLayoutParams.rightMargin, fabLayoutParams.bottomMargin);

        // Get FAB's coordinates
        int[] fabCoords = new int[2];
        mFab.getLocationOnScreen(fabCoords);


        // Get sheet's coordinates
        int[] sheetCoords = new int[2];
        mRevealView.getLocationOnScreen(sheetCoords);

        int leftDiff = Math.max(sheetCoords[0] - fabCoords[0], fabLayoutParams.leftMargin);
        int rightDiff = Math.min((sheetCoords[0] + mRevealView.getWidth()) - (fabCoords[0] + mFab.getWidth()), -fabLayoutParams.rightMargin);
        int topDiff = Math.max(sheetCoords[1] - fabCoords[1], fabLayoutParams.topMargin);
        int bottomDiff = Math.min((sheetCoords[1] + mRevealView.getHeight()) - (fabCoords[1] + mFab.getHeight()), -fabLayoutParams.bottomMargin);

        float sheetX = mRevealView.getX();
        float sheetY = mRevealView.getY();

        if (mRevealDirection == RevealDirection.LEFT || mRevealDirection == RevealDirection.UP) {
            // Align the right side of the sheet with the right side of the FAB
            mRevealView.setX(sheetX - rightDiff - sheetLayoutParams.rightMargin);
            // Align the bottom of the sheet with the bottom of the FAB
            mRevealView.setY(sheetY - bottomDiff - sheetLayoutParams.bottomMargin);
        } else if (mRevealDirection == RevealDirection.RIGHT) {
            // align the left side of the sheet with the left side of the FAB
            mRevealView.setX(sheetX - leftDiff + sheetLayoutParams.leftMargin);
            // Align the bottom of the sheet with the bottom of the FAB
            mRevealView.setY(sheetY - bottomDiff - sheetLayoutParams.bottomMargin);
        } else if (mRevealDirection == RevealDirection.DOWN) {
            // align the top of the sheet with the top of the FAB
            mRevealView.setY(sheetY - topDiff + sheetLayoutParams.topMargin);
            // Align the right side of the sheet with the right side of the FAB
            mRevealView.setX(sheetX - rightDiff - sheetLayoutParams.rightMargin);

        }
    }

    private int dpToPx(Context mContext, int dp) {
        DisplayMetrics displayMetrics = mContext.getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }
}
