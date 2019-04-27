package mjaruijs.homecontrol.activities

import android.view.animation.Animation

class AnimationListener(private val onFinish: () -> Unit) : Animation.AnimationListener {
    override fun onAnimationRepeat(animation: Animation) {}

    override fun onAnimationEnd(animation: Animation) {
        onFinish()
    }

    override fun onAnimationStart(animation: Animation) {}
}