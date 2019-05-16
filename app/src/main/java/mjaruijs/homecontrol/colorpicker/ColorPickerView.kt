package mjaruijs.homecontrol.colorpicker

import android.content.Context
import android.view.LayoutInflater
import mjaruijs.homecontrol.R

class ColorPickerView(context: Context) {

    private val colorPickerPalette: ColorPickerPalette
    private val colors = IntArray(20)

    init {
        for (i in 0 until ColorList.colors.size) {
            val color = ColorList.colors[i]
            colors[i] = color.intValue
        }

        val layoutInflater = LayoutInflater.from(context)
        colorPickerPalette = layoutInflater.inflate(R.layout.custom_picker, null) as ColorPickerPalette
        colorPickerPalette.init(colors.size, 5, object : ColorPickerSwatch.OnColorSelectedListener {
            override fun onColorSelected(color: Int) {}
        })
        colorPickerPalette.drawPalette(colors, -1)
    }

    fun getView() = colorPickerPalette

    fun setOnClickListener(listener: ColorPickerSwatch.OnColorSelectedListener) {
        colorPickerPalette.mOnColorSelectedListener = listener
        colorPickerPalette.drawPalette(colors, -1)
    }

}