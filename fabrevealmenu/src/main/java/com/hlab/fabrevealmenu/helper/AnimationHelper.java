package com.hlab.fabrevealmenu.helper;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.graphics.Point;
import android.os.Build;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;

import com.hlab.fabrevealmenu.enums.Direction;
import com.hlab.fabrevealmenu.listeners.AnimationListener;

import io.codetail.animation.SupportAnimator;
import io.codetail.animation.arcanimator.ArcAnimator;
import io.codetail.animation.arcanimator.Side;

public class AnimationHelper {

    private final int FAB_ARC_DEGREES = -30;
    // Animation durations
    public static final int REVEAL_DURATION = 600;
    public static final int FAB_ANIM_DURATION = (int) (REVEAL_DURATION / 2.4);
    private final float FAB_SCALE_FACTOR = 0.2f;
    private final int OVERLAY_ANIM_DURATION = REVEAL_DURATION / 2;

    //View specific fields
    private int anchorX;
    private int anchorY;
    private int centerX;
    private int centerY;

    private ViewHelper viewHelper;

    private Interpolator fabAnimInterpolator, revealAnimInterpolator;

    public AnimationHelper(ViewHelper viewHelper) {
        this.viewHelper = viewHelper;
        fabAnimInterpolator = new AccelerateDecelerateInterpolator();
        revealAnimInterpolator = new FastOutLinearInInterpolator();
    }

    public void moveFab(View fabView, View revealView, Direction direction, boolean isReturning, final AnimationListener listener) {
        float scaleFactor = isReturning ? -FAB_SCALE_FACTOR : FAB_SCALE_FACTOR;
        float alpha = isReturning ? 1.0f : 1.0f;
        float endX = 0;
        float endY = 0;

        if (!isReturning) {
            Point anchorPoint = viewHelper.updateFabAnchor(fabView);
            anchorX = anchorPoint.x;
            anchorY = anchorPoint.y;
            endX = revealView.getX() + centerX;
            endY = revealView.getY() + centerY;
        } else {
            endX = anchorX;
            endY = anchorY;
        }
        //move by arc animation
        startArcAnim(fabView, endX, endY, FAB_ARC_DEGREES, getArcSide(direction), FAB_ANIM_DURATION, fabAnimInterpolator, listener);
        //scale fab
        fabView.animate().scaleXBy(scaleFactor).scaleYBy(scaleFactor).alpha(alpha).setDuration(FAB_ANIM_DURATION).start();
    }

    public void revealMenu(View view, float startRadius, float endRadius, boolean isReturning, final AnimationListener listener) {
        //start circular reveal animation
        startCircularRevealAnim(view, centerX, centerY, startRadius, endRadius, REVEAL_DURATION, revealAnimInterpolator, listener);
    }

    private void startCircularRevealAnim(View view, int centerX, int centerY, float startRadius,
                                         float endRadius, long duration, Interpolator interpolator, final AnimationListener listener) {
        // Use native circular reveal on Android 5.0+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // Native circular reveal uses coordinates relative to the view
            int relativeCenterX = (int) (centerX - view.getX());
            int relativeCenterY = (int) (centerY - view.getY());
            // Setup animation
            Animator anim = ViewAnimationUtils.createCircularReveal(view, relativeCenterX,
                    relativeCenterY, startRadius, endRadius);
            anim.setDuration(duration);
            anim.setInterpolator(interpolator);
            // Add listener
            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    if (listener != null) {
                        listener.onStart();
                    }
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    if (listener != null) {
                        listener.onEnd();
                    }
                }
            });
            // Start animation
            anim.start();
        } else {
            // Circular reveal library uses absolute coordinates
            // Setup animation
            SupportAnimator anim = io.codetail.animation.ViewAnimationUtils
                    .createCircularReveal(view, centerX, centerY, startRadius, endRadius);
            anim.setDuration((int) duration);
            anim.setInterpolator(interpolator);
            // Add listener
            anim.addListener(new SupportAnimator.SimpleAnimatorListener() {
                @Override
                public void onAnimationStart() {
                    if (listener != null) {
                        listener.onStart();
                    }
                }

                @Override
                public void onAnimationEnd() {
                    if (listener != null) {
                        listener.onEnd();
                    }
                }
            });
            // Start animation
            anim.start();
        }
    }

    private void startArcAnim(View view, float endX, float endY, float degrees, Side side,
                              long duration, Interpolator interpolator, final AnimationListener listener) {
        // Setup animation
        // Cast end coordinates to ints so that the FAB will be animated to the same position even
        // when there are minute differences in the coordinates
        ArcAnimator anim = ArcAnimator.createArcAnimator(view, (int) endX, (int) endY, degrees,
                side);
        anim.setDuration(duration);
        anim.setInterpolator(interpolator);
        // Add listener
        anim.addListener(new com.nineoldandroids.animation.AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(com.nineoldandroids.animation.Animator animation) {
                if (listener != null) {
                    listener.onStart();
                }
            }

            @Override
            public void onAnimationEnd(com.nineoldandroids.animation.Animator animation) {
                if (listener != null) {
                    listener.onEnd();
                }
            }
        });
        // Start animation
        anim.start();
    }

    public void showOverlay(final View mOverlayLayout) {
        mOverlayLayout.animate().alpha(1).setDuration(OVERLAY_ANIM_DURATION).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                mOverlayLayout.setVisibility(View.VISIBLE);
            }
        });
    }

    public void hideOverlay(final View mOverlayLayout) {
        mOverlayLayout.animate().alpha(0).setDuration(OVERLAY_ANIM_DURATION * 2).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mOverlayLayout.setVisibility(View.GONE);
            }
        });
    }

    public void calculateCenterPoints(View viewToReveal, Direction mDirection) {
        centerX = viewHelper.getSheetRevealCenterX(viewToReveal, mDirection);
        centerY = viewHelper.getSheetRevealCenterY(viewToReveal, mDirection);
    }

    private Side getArcSide(Direction mDirection) {
        if (mDirection == Direction.LEFT || mDirection == Direction.UP)
            return Side.RIGHT;
        else
            return Side.LEFT;
    }

}
