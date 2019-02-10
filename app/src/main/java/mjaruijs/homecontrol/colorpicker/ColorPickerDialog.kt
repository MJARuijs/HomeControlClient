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

import android.app.AlertDialog
import android.app.Dialog
import android.app.DialogFragment
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ProgressBar

import mjaruijs.homecontrol.R


/**
 * A dialog which takes in as input an array of colors and creates a palette allowing the user to
 * select a specific color swatch, which invokes a listener.
 */
class ColorPickerDialog : DialogFragment(), ColorPickerSwatch.OnColorSelectedListener {

    private lateinit var mAlertDialog: AlertDialog

    private var mTitleResId = R.string.color_picker_default_title
    private var mColors: IntArray? = null
    private var mColorContentDescriptions: Array<String>? = null
    private var mSelectedColor: Int = 0
    private var mColumns: Int = 0
    private var mSize: Int = 0

    private var mPalette: ColorPickerPalette? = null
    private var mProgress: ProgressBar? = null

    private var mListener: ColorPickerSwatch.OnColorSelectedListener? = null

    var colors: IntArray?
        get() = mColors
        set(colors) {
            if (!mColors!!.contentEquals(colors!!)) {
                mColors = colors
                refreshPalette()
            }
        }

    var selectedColor: Int
        get() = mSelectedColor
        set(color) {
            if (mSelectedColor != color) {
                mSelectedColor = color
                refreshPalette()
            }
        }

    fun initialize(titleResId: Int, colors: IntArray, selectedColor: Int, columns: Int, size: Int) {
        setArguments(titleResId, columns, size)
        setColors(colors, selectedColor)
    }

    fun setArguments(titleResId: Int, columns: Int, size: Int) {
        val bundle = Bundle()
        bundle.putInt(KEY_TITLE_ID, titleResId)
        bundle.putInt(KEY_COLUMNS, columns)
        bundle.putInt(KEY_SIZE, size)
        arguments = bundle
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (arguments != null) {
            mTitleResId = arguments.getInt(KEY_TITLE_ID)
            mColumns = arguments.getInt(KEY_COLUMNS)
            mSize = arguments.getInt(KEY_SIZE)
        }

        if (savedInstanceState != null) {
            mColors = savedInstanceState.getIntArray(KEY_COLORS)
            mSelectedColor = savedInstanceState.getSerializable(KEY_SELECTED_COLOR) as Int
            mColorContentDescriptions = savedInstanceState.getStringArray(
                    KEY_COLOR_CONTENT_DESCRIPTIONS)
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle): Dialog {
        val activity = activity

        val view = LayoutInflater.from(getActivity()).inflate(R.layout.color_picker_dialog, null)
        mProgress = view.findViewById(android.R.id.progress)
        mPalette = view.findViewById(R.id.color_picker)
        mPalette!!.init(mSize, mColumns, this)

        if (mColors != null) {
            showPaletteView()
        }

        mAlertDialog = AlertDialog.Builder(activity)
                .setTitle(mTitleResId)
                .setView(view)
                .create()

        return mAlertDialog
    }

    override fun onColorSelected(color: Int) {
        Log.i(javaClass.simpleName, "ColorValues picker selected!")
        if (mListener != null) {
            mListener!!.onColorSelected(color)
        }

        if (targetFragment is ColorPickerSwatch.OnColorSelectedListener) {
            val listener = targetFragment as ColorPickerSwatch.OnColorSelectedListener
            listener.onColorSelected(color)
        }

        if (color != mSelectedColor) {
            mSelectedColor = color
            // Redraw palette to show checkmark on newly selected color before dismissing.
            mPalette!!.drawPalette(mColors, mSelectedColor)
        }

        dismiss()
    }

    fun showPaletteView() {
        if (mProgress != null && mPalette != null) {
            mProgress!!.visibility = View.GONE
            refreshPalette()
            mPalette!!.visibility = View.VISIBLE
        }
    }

    fun showProgressBarView() {
        if (mProgress != null && mPalette != null) {
            mProgress!!.visibility = View.VISIBLE
            mPalette!!.visibility = View.GONE
        }
    }

    fun setColors(colors: IntArray, selectedColor: Int) {
        if (!mColors!!.contentEquals(colors) || mSelectedColor != selectedColor) {
            mColors = colors
            mSelectedColor = selectedColor
            refreshPalette()
        }
    }

    fun setColorContentDescriptions(colorContentDescriptions: Array<String>) {
        if (!mColorContentDescriptions!!.contentEquals(colorContentDescriptions)) {
            mColorContentDescriptions = colorContentDescriptions
            refreshPalette()
        }
    }

    private fun refreshPalette() {
        if (mPalette != null && mColors != null) {
            mPalette!!.drawPalette(mColors, mSelectedColor, mColorContentDescriptions)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putIntArray(KEY_COLORS, mColors)
        outState.putSerializable(KEY_SELECTED_COLOR, mSelectedColor)
        outState.putStringArray(KEY_COLOR_CONTENT_DESCRIPTIONS, mColorContentDescriptions)
    }

    companion object {

        val SIZE_LARGE = 1
        val SIZE_SMALL = 2

        private const val KEY_TITLE_ID = "title_id"
        private const val KEY_COLORS = "colors"
        private const val KEY_COLOR_CONTENT_DESCRIPTIONS = "color_content_descriptions"
        private const val KEY_SELECTED_COLOR = "selected_color"
        private const val KEY_COLUMNS = "columns"
        private const val KEY_SIZE = "size"

        fun newInstance(titleResId: Int, colors: IntArray, selectedColor: Int,
                        columns: Int, size: Int): ColorPickerDialog {
            val ret = ColorPickerDialog()
            ret.initialize(titleResId, colors, selectedColor, columns, size)
            return ret
        }
    }
}