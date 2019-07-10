package mjaruijs.homecontrol.settings

import android.annotation.SuppressLint
import android.content.Context
import android.support.constraint.ConstraintLayout
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent.*
import android.view.View
import android.view.ViewGroup
import mjaruijs.homecontrol.R

@SuppressLint("InflateParams")
class ClickableSwitchPreference(context: Context, attributes: AttributeSet) : SwitchPreference(context, attributes) {

    init {
        val layoutInflater = LayoutInflater.from(context)
        view = layoutInflater.inflate(R.layout.clickable_switch_preference, null, false)
        textView = view.findViewById(R.id.clickable_switch_title)
        switch = view.findViewById(R.id.clickable_switch)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(parent: ViewGroup): View {
        super.onCreateView(parent)
        val layoutInflater = LayoutInflater.from(context)

        view = layoutInflater.inflate(R.layout.clickable_switch_preference, parent, false)
        textView = view.findViewById(R.id.clickable_switch_title)
        switch = view.findViewById(R.id.clickable_switch)
        switch.isChecked = isChecked

        switch.setOnClickListener {
            onClickListener.invoke()
        }

        val layout = view.findViewById(R.id.clickable_switch_preference) as ConstraintLayout
        layout.setOnTouchListener { v, event ->
            when {
                event.action == ACTION_DOWN -> v.setBackgroundColor(android.graphics.Color.rgb(230, 230, 230))
                event.action == ACTION_UP -> {
                    v.setBackgroundColor(-1)
                    // TODO: Open dialog which shows settings for ledstrip
                }
                event.action == ACTION_CANCEL -> v.setBackgroundColor(-1)
            }
            true
        }

        textView.text = title

        return view
    }

}