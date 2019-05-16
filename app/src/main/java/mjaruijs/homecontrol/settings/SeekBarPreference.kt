package mjaruijs.homecontrol.settings

import android.annotation.SuppressLint
import android.content.Context
import android.content.Context.LAYOUT_INFLATER_SERVICE
import android.preference.Preference
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import mjaruijs.homecontrol.R

class SeekBarPreference(context: Context, attributes: AttributeSet) : Preference(context, attributes), SeekBar.OnSeekBarChangeListener {

    private lateinit var seekBar: SeekBar

    override fun onBindView(view: View) {
        super.onBindView(view)
        seekBar = view.findViewById(R.id.brightnessSeekbar) as SeekBar
    }

    override fun onCreateView(parent: ViewGroup): View {
        super.onCreateView(parent)
        val layoutInflater = context.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        return layoutInflater.inflate(R.layout.seek_bar_preference, parent, false)
    }

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {
    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {
    }
}