package mjaruijs.homecontrol.settings

import android.annotation.SuppressLint
import android.content.Context
import android.preference.Preference
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import android.widget.TextView
import mjaruijs.homecontrol.R

@SuppressLint("InflateParams")
open class SwitchPreference(context: Context, attributes: AttributeSet) : Preference(context, attributes) {

    protected var view: View
    protected var switch: Switch
    protected var textView: TextView

    private var title = "title"

    var isChecked = false
        get() {
            return switch.isChecked
        }
        set(value) {
            field = value
            switch.isChecked = value
            println("IS CHECKED: $title   ${switch.isChecked}")
        }

    var onClickListener: () -> Unit = {}
        set(value) {
            field = value
            switch.setOnClickListener {
                value.invoke()
            }
        }

    init {
        val layoutInflater = LayoutInflater.from(context)
        view = layoutInflater.inflate(R.layout.switch_preference, null, false)
        textView = view.findViewById(R.id.switch_title)
        switch = view.findViewById(R.id.switch_preference)
    }

    fun setText(text: String) {
        title = text
        textView.text = text
    }

    override fun onCreateView(parent: ViewGroup): View {
        super.onCreateView(parent)
        val layoutInflater = LayoutInflater.from(context)

        view = layoutInflater.inflate(R.layout.switch_preference, parent, false)
        textView = view.findViewById(R.id.switch_title)
        switch = view.findViewById(R.id.switch_preference)
        switch.isChecked = isChecked
        switch.setOnClickListener {
            onClickListener.invoke()
        }

        textView.text = title
        return view
    }

}