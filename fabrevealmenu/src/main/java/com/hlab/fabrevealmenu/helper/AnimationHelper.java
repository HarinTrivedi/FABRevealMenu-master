package com.hlab.fabrevealmenu.helper;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;

import androidx.interpolator.view.animation.FastOutSlowInInterpolator;
import androidx.transition.TransitionManager;

import com.google.android.material.transition.MaterialArcMotion;
import com.google.android.material.transition.MaterialContainerTransform;

public class AnimationHelper {

    private final int duration;

    public AnimationHelper(int duration) {
        this.duration = duration;
    }

    public void revealMenu(ViewGroup baseView, View startView, View endView, boolean isReturning) {
        //start circular reveal animation

        // Construct a container transform transition between two views.
        MaterialContainerTransform transition = buildContainerTransform();
        transition.setStartView(isReturning ? endView : startView);
        transition.setEndView(isReturning ? startView : endView);

        // Add a single target to stop the container transform from running on both the start
        // and end view.
        transition.addTarget(startView);

        // Trigger the container transform transition.
        TransitionManager.beginDelayedTransition(baseView, transition);
        startView.setVisibility(isReturning ? View.VISIBLE : View.INVISIBLE);
        endView.setVisibility(isReturning ? View.INVISIBLE : View.VISIBLE);
    }

    public void showOverlay(final View mOverlayLayout) {
        mOverlayLayout.animate().alpha(1).setDuration(duration).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                mOverlayLayout.setVisibility(View.VISIBLE);
            }
        });
    }

    public void hideOverlay(final View mOverlayLayout) {
        mOverlayLayout.animate().alpha(0).setDuration(duration).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mOverlayLayout.setVisibility(View.GONE);
            }
        });
    }

    private MaterialContainerTransform buildContainerTransform() {
        MaterialContainerTransform transform = new MaterialContainerTransform();
        transform.setScrimColor(Color.TRANSPARENT);
        transform.setDuration(duration);
        transform.setInterpolator(new FastOutSlowInInterpolator());
        transform.setPathMotion(new MaterialArcMotion());
        transform.setFadeMode(MaterialContainerTransform.FADE_MODE_THROUGH);
        return transform;
    }

}
