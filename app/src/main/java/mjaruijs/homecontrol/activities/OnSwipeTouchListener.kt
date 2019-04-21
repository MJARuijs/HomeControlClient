package mjaruijs.homecontrol.activities

import android.animation.ObjectAnimator
import android.view.MotionEvent
import android.view.MotionEvent.*
import android.view.View
import kotlinx.android.synthetic.main.card_view.view.*
import mjaruijs.homecontrol.R

class OnSwipeTouchListener : View.OnTouchListener {

    private val animationDuration = 250L

    private var dX = 0.0f

    private var expanded = false
    private var expanding = false
    private var collapsing = false

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        v.performClick()
        when (event.action) {
            ACTION_DOWN -> {
                dX = v.card_view_layout.x - event.rawX
            }
            ACTION_MOVE -> {
                val translation = Math.max(-240.0f, Math.min((event.rawX + dX), 0.0f))
                v.delete_button.translationX = translation + 240
                v.delete_background.translationX = translation + 240
                translateViews(Pair(v.card_view_layout, translation), animationLength = 0L)
            }
            ACTION_UP -> {
                if (v.card_view_layout.x >= -240.0f && !expanded && event.rawX + dX < 10.0f) {
                    translateViews(
                            Pair(v.findViewById(R.id.delete_background) as View, -1.0f),
                            Pair(v.findViewById(R.id.delete_button) as View, -1.0f),
                            Pair(v.card_view_layout, -240.0f)
                    )
                    expanding = true
                } else if (expanded && event.rawX + dX > -250.0f) {
                    translateViews(
                            Pair(v.findViewById(R.id.delete_background) as View, 240.0f),
                            Pair(v.findViewById(R.id.delete_button) as View, 240.0f),
                            Pair(v.card_view_layout, 0.0f)
                    )
                    collapsing = true
                }

                if (expanding) {
                    expanded = true
                } else if (collapsing) {
                    expanded = false
                }

                expanding = false
                collapsing = false
            }
            ACTION_CANCEL -> {
                expanded = if (v.card_view_layout.x >= -120.0f) {
                    translateViews(
                            Pair(v.findViewById(R.id.delete_background) as View, 240.0f),
                            Pair(v.findViewById(R.id.delete_button) as View, 240.0f),
                            Pair(v.card_view_layout, 0.0f)
                    )
                    false
                } else {
                    translateViews(
                            Pair(v.findViewById(R.id.delete_background) as View, -1.0f),
                            Pair(v.findViewById(R.id.delete_button) as View, -1.0f),
                            Pair(v.card_view_layout, -240.0f)
                    )
                    true
                }
            }
        }

        return true
    }

    private fun translateViews(vararg translations: Pair<View, Float>, animationLength: Long = animationDuration) {
        for (translation in translations) {
            ObjectAnimator.ofFloat(translation.first, "translationX", translation.second).apply {
                duration = animationLength
            }.start()
        }
    }
}