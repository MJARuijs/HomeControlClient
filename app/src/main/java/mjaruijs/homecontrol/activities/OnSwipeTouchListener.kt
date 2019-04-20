package mjaruijs.homecontrol.activities

import android.view.MotionEvent
import android.view.MotionEvent.*
import android.view.View
import kotlinx.android.synthetic.main.card_view.view.*

class OnSwipeTouchListener : View.OnTouchListener {

    private var dX = 0.0f
    private var dY = 0.0f

    private var expanded = false
    private var expanding = false
    private var collapsing = false

    override fun onTouch(v: View, event: MotionEvent): Boolean {

        when (event.action) {
            ACTION_DOWN -> {
                dX = v.card_view_layout.x - event.rawX
                dY = v.card_view_layout.y - event.rawY
            }
            ACTION_MOVE -> {
                val translation = Math.max(-240.0f, Math.min((event.rawX + dX), 0.0f))
                v.delete_button.translationX = translation + 240
                v.delete_background.translationX = translation + 240

                v.card_view_layout.animate()
                        .x(translation)
                        .setDuration(0)
                        .start()
            }
            ACTION_UP -> {
                val translation = Math.max(-240.0f, Math.min((event.rawX + dX), 0.0f))
//                v.delete_button.translationX = translation + 240 - v.delete_button.translationX
//                v.delete_background.translationX = translation + 240 - v.delete_background.translationX

                if (v.card_view_layout.x >= -240.0f && !expanded && event.rawX + dX < 10.0f) {
                    v.card_view_layout.animate()
                            .x(-240.0f)
                            .setDuration(3000)
                            .start()
                    expanding = true
                } else if (expanded && event.rawX + dX > -250.0f) {
                    v.card_view_layout.animate()
                            .x(0.0f)
                            .setDuration(3000)
                            .start()
                    collapsing = true
                }

                if (expanding) {
                    expanded = true
                    println("EXPANDED")
                    v.delete_button.translationX = 0.0f
                    v.delete_background.translationX = 0.0f
                } else if (collapsing) {
                    expanded = false
                    println("COLLAPSED")
                    v.delete_button.translationX = 240.0f
                    v.delete_background.translationX = 240.0f
                } else {
                    if (v.card_view_layout.x <= -240.0f) {
                        println("EXPANDED")
                        v.delete_button.translationX = 0.0f
                        v.delete_background.translationX = 0.0f
                        expanded = true
                    }
                    if (v.card_view_layout.x == 0.0f) {
                        println("NOT EXPANDED")
                        v.delete_button.translationX = 240.0f
                        v.delete_background.translationX = 240.0f
                        expanded = false
                    }
                }
                expanding = false
                collapsing = false
            }
        }



        return false
    }
}