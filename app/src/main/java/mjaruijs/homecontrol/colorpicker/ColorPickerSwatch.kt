/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package mjaruijs.homecontrol.colorpicker

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView

import mjaruijs.homecontrol.R

/**
 * Creates a circular swatch of a specified color.  Adds a checkmark if marked as checked.
 */
class ColorPickerSwatch : FrameLayout, View.OnClickListener {
    private var mColor: Int = 0
    private var mSwatchImage: ImageView? = null
    private lateinit var mCheckmarkImage: ImageView
    private lateinit var mOnColorSelectedListener: OnColorSelectedListener

    /**
     * Interface for a callback when a color square is selected.
     */
    interface OnColorSelectedListener {
        fun onColorSelected(color: Int)
    }

    constructor(context: Context, color: Int) : super(context) {
        mColor = color

        LayoutInflater.from(context).inflate(R.layout.color_picker_swatch, this)
        mSwatchImage = findViewById<View>(R.id.color_picker_swatch) as ImageView
        setColor(color)
    }

    constructor(context: Context, color: Int, checked: Boolean,
                listener: OnColorSelectedListener) : super(context) {
        mColor = color
        mOnColorSelectedListener = listener

        LayoutInflater.from(context).inflate(R.layout.color_picker_swatch, this)
        mSwatchImage = findViewById<View>(R.id.color_picker_swatch) as ImageView
        mCheckmarkImage = findViewById<View>(R.id.color_picker_checkmark) as ImageView
        setColor(color)
        setChecked(checked)
        setOnClickListener(this)
    }

    fun setColor(color: Int) {
        val colorDrawable = arrayOf(context.resources.getDrawable(R.drawable.color_picker_swatch))
        mSwatchImage!!.setImageDrawable(ColorStateDrawable(colorDrawable, color))
    }

    private fun setChecked(checked: Boolean) {
        if (checked) {
            mCheckmarkImage.visibility = View.VISIBLE
        } else {
            mCheckmarkImage.visibility = View.GONE
        }
    }

    override fun onClick(v: View) {
        mOnColorSelectedListener.onColorSelected(mColor)
    }
}
