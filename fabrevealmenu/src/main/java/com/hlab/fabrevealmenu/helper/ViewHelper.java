package com.hlab.fabrevealmenu.helper;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.hlab.fabrevealmenu.R;
import com.hlab.fabrevealmenu.enums.Direction;

import io.codetail.widget.RevealLinearLayout;

public class ViewHelper {

    private Context mContext;
    private final int SHEET_REVEAL_OFFSET_Y = 5;
    private int const_val = 1;

    //common layout parameter
    private ViewGroup.LayoutParams matchParams = null;
    private ViewGroup.LayoutParams wrapParams = null;

    public ViewHelper(Context context) {
        mContext = context;
        matchParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        wrapParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        const_val = (int) ((SHEET_REVEAL_OFFSET_Y - 1.5) / SHEET_REVEAL_OFFSET_Y);
    }

    public CardView generateBaseView() {
        //Base view
        CardView mBaseView = new CardView(mContext);
        mBaseView.setLayoutParams(matchParams);
        mBaseView.setCardElevation(dpToPx(mContext, 5));
        mBaseView.setRadius(mContext.getResources().getDimension(R.dimen.card_radius));
        return mBaseView;
    }

    public RecyclerView generateMenuView() {
        //Create menu view
        RecyclerView mMenuView = new RecyclerView(mContext);
        mMenuView.setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);
        mMenuView.setBackgroundColor(Color.TRANSPARENT);
        mMenuView.setLayoutParams(matchParams);
        return mMenuView;
    }

    public RevealLinearLayout generateRevealView() {
        //Reveal view
        RevealLinearLayout mRevealView = new RevealLinearLayout(mContext);
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
    public void alignMenuWithFab(View mFab, View mRevealView, Direction mDirection) {
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

        if (mDirection == Direction.LEFT || mDirection == Direction.UP) {
            // Align the right side of the sheet with the right side of the FAB
            mRevealView.setX(sheetX - rightDiff - sheetLayoutParams.rightMargin);
            // Align the bottom of the sheet with the bottom of the FAB
            mRevealView.setY(sheetY - bottomDiff - sheetLayoutParams.bottomMargin);
        } else if (mDirection == Direction.RIGHT) {
            // align the left side of the sheet with the left side of the FAB
            mRevealView.setX(sheetX - leftDiff + sheetLayoutParams.leftMargin);
            // Align the bottom of the sheet with the bottom of the FAB
            mRevealView.setY(sheetY - bottomDiff - sheetLayoutParams.bottomMargin);
        } else if (mDirection == Direction.DOWN) {
            // align the top of the sheet with the top of the FAB
            mRevealView.setY(sheetY - topDiff + sheetLayoutParams.topMargin);
            // Align the right side of the sheet with the right side of the FAB
            mRevealView.setX(sheetX - rightDiff - sheetLayoutParams.rightMargin);

        }
//        if (rightDiff != 0) {
//            float sheetX = mRevealView.getX();
//            // Align the right side of the sheet with the right side of the FAB if
//            // doing so would not move the sheet off the screen
//            if (rightDiff <= sheetX) {
//                mRevealView.setX(sheetX - rightDiff - sheetLayoutParams.rightMargin);
//                //revealXDirection = RevealXDirection.LEFT;
//            }
//            // Otherwise, align the left side of the sheet with the left side of the FAB
//            else if (leftDiff != 0 && leftDiff <= sheetX) {
//                mRevealView.setX(sheetX - leftDiff + sheetLayoutParams.leftMargin);
//                //revealXDirection = RevealXDirection.RIGHT;
//            }
//        }
//
//        if (bottomDiff != 0) {
//            float sheetY = mRevealView.getY();
//            // Align the bottom of the sheet with the bottom of the FAB
//            if (bottomDiff <= sheetY) {
//                mRevealView.setY(sheetY - bottomDiff - sheetLayoutParams.bottomMargin);
//                //revealYDirection = RevealYDirection.UP;
//            }
//            // Otherwise, align the top of the sheet with the top of the FAB
//            else if (topDiff != 0 && topDiff <= sheetY) {
//                mRevealView.setY(sheetY - topDiff + sheetLayoutParams.topMargin);
//                //revealYDirection = RevealYDirection.DOWN;
//            }
//        }
    }

    public int getSheetRevealCenterX(View view, Direction mDirection) {
        if (mDirection == Direction.LEFT)
            return (int) (view.getX() + (view.getWidth() / 2) + (view.getWidth() * const_val));
        else if (mDirection == Direction.RIGHT)
            return (int) (view.getX() + (view.getWidth() / 2) - (view.getWidth() * const_val));
        else
            return (int) (view.getX() + (view.getWidth() / 2));
    }

    public int getSheetRevealCenterY(View view, Direction mDirection) {
        if (mDirection == Direction.UP)
            return (int) (view.getY() + (view.getHeight() / 2) + (view.getHeight() * const_val));
        else if (mDirection == Direction.DOWN)
            return (int) (view.getY() + (view.getHeight() / 2) - (view.getHeight() * const_val));
        else
            return (int) (view.getY() + (view.getHeight() / 2));
    }

    public Point updateFabAnchor(View mFabView) {
        // Update the anchor with the current translation
        return setFabAnchor(mFabView, mFabView.getTranslationX(), mFabView.getTranslationY());
    }


    public Point setFabAnchor(View mFabView, float translationX, float translationY) {
        int anchorX = Math
                .round(mFabView.getX() + (mFabView.getWidth() / 2) + (translationX - mFabView.getTranslationX()));
        int anchorY = Math
                .round(mFabView.getY() + (mFabView.getHeight() / 2) + (translationY - mFabView.getTranslationY()));

        return new Point(anchorX, anchorY);
    }

    public int dpToPx(Context mContext, int dp) {
        DisplayMetrics displayMetrics = mContext.getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }

    public int pxToDp(Context mContext, int px) {
        DisplayMetrics displayMetrics = mContext.getResources().getDisplayMetrics();
        int dp = Math.round(px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return dp;
    }
}
