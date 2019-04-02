package mjaruijs.homecontrol.activities

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.*
import android.widget.Toast
import mjaruijs.homecontrol.R
import mjaruijs.homecontrol.activities.lampsetup.LampActivity
import mjaruijs.homecontrol.colorpicker.ColorList
import mjaruijs.homecontrol.colorpicker.ColorPickerPalette
import mjaruijs.homecontrol.colorpicker.ColorPickerSwatch
import mjaruijs.homecontrol.networking.MessageSender
import mjaruijs.homecontrol.settings.PreferenceFragment
import mjaruijs.homecontrol.networking.server.EndConnection

class MainActivity : AppCompatActivity() {

    private val preferenceFragment = PreferenceFragment()

    private var backPressedTime: Long = -1
    private var backPressed: Boolean = false

    private lateinit var colorPicker: ColorPicker

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.main_activity)

        fragmentManager.beginTransaction()
                .replace(R.id.preferences_holder, preferenceFragment)
                .commitAllowingStateLoss()

        colorPicker = ColorPicker()
    }

    public override fun onStop() {
        super.onStop()
        EndConnection().execute()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.options_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.synchronize -> preferenceFragment.synchronizeSettings()
            R.id.lamp_setup -> startActivity(Intent(applicationContext, LampActivity::class.java))
            R.id.led_strip_setup -> {
                colorPicker.init(this)
                colorPicker.show()
            }
            else -> super.onOptionsItemSelected(item)
        }
        return true
    }

    override fun onBackPressed() {
        if (backPressed) {
            EndConnection().execute()
            finish()
        } else {
            backPressedTime = System.currentTimeMillis()
            backPressed = true
            Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show()
        }
    }

    @SuppressLint("InflateParams")
    inner class ColorPicker {

        private var selectedColor = 0
        private var colorPickerPalette: ColorPickerPalette? = null
        private var colorAlertDialog: AlertDialog? = null

        private var colors = IntArray(20)

        fun init(context: Context) {
            initColors()

            val layoutInflater = LayoutInflater.from(context)
            colorPickerPalette = layoutInflater.inflate(R.layout.custom_picker, null) as ColorPickerPalette
            colorPickerPalette!!.init(colors.size, 5, object : ColorPickerSwatch.OnColorSelectedListener {
                override fun onColorSelected(color: Int) {
                    println(color)
                    println(ColorList.getByIntValue(color).toString())
                    selectedColor = color
                    MessageSender(object : MessageSender.ConnectionResponse {
                        override fun result(message: String) {
                            colorAlertDialog!!.dismiss()
                        }
                    }).execute("StudyRoom", "led_strip", ColorList.getByIntValue(color).toString())
                }
            })

            colorPickerPalette!!.drawPalette(colors, selectedColor)

            colorAlertDialog = AlertDialog.Builder(context, R.style.Alert_Dialog_Dark)
                    .setTitle("Select a Color")
                    .setView(colorPickerPalette)
                    .create()
        }

        fun show() {
            colorAlertDialog!!.show()
        }

        private fun initColors() {
            for ((index, color) in ColorList.colors.withIndex()) {
                colors[index] = color.intValue
            }
        }
    }

}
