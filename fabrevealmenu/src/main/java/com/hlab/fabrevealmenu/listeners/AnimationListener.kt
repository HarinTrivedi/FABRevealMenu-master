package com.hlab.fabrevealmenu.listeners

/**
 * Listener for animation events.
 */
abstract class AnimationListener {

    /**
     * Called when the animation starts.
     */
    open fun onStart() {}

    /**
     * Called when the animation ends.
     */
    open fun onEnd() {}
    //
    //	public abstract void onAnimationStart(Animator animation);
    //
    //	public abstract void onAnimationCancel(Animator animation);
    //
    //	public abstract void onAnimationEnd(Animator animation);
}
