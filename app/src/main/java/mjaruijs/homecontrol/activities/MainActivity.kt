package mjaruijs.homecontrol.activities

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import mjaruijs.homecontrol.R
import mjaruijs.homecontrol.activities.lampsetup.LampActivity
import mjaruijs.homecontrol.colorpicker.ColorList
import mjaruijs.homecontrol.colorpicker.ColorPickerPalette
import mjaruijs.homecontrol.colorpicker.ColorPickerSwatch
import mjaruijs.homecontrol.networking.NetworkManager
import mjaruijs.homecontrol.services.ExceptionHandler
import mjaruijs.homecontrol.settings.PreferenceFragment

class MainActivity : AppCompatActivity() {

    private val preferenceFragment = PreferenceFragment()

    private var backPressedTime = -1L
    private var backPressed = false
    private var userLeft = false

    private lateinit var colorPicker: ColorPicker

    private val className = "preference_fragment"

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.main_activity)

        fragmentManager.beginTransaction()
                .replace(R.id.preferences_holder, preferenceFragment)
                .commitAllowingStateLoss()

        colorPicker = ColorPicker()
        Thread.setDefaultUncaughtExceptionHandler(ExceptionHandler())

        Thread {
//            InstalledAppsCache.get(this)
        }.start()
    }

    public override fun onStop() {
        super.onStop()
        if (!userLeft) {
//            NetworkManager.addSendOnlyMessage("close_connection")
        }
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
                colorPicker.init(this, 1)
                colorPicker.show()
            }
//            R.id.lamp_setup -> {
//                colorPicker.init(this, 2)
//                colorPicker.show()
//            }
            else -> super.onOptionsItemSelected(item)
        }
        return true
    }

    override fun onBackPressed() {
        if (backPressed) {
            userLeft = true
            finish()
        } else {
            backPressedTime = System.currentTimeMillis()
            backPressed = true
            Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show()
        }
    }

    @SuppressLint("InflateParams")
    inner class ColorPicker {

        private var selectedColor1 = 0
        private var selectedColor2 = 0
        private var colorPickerPalette: ColorPickerPalette? = null
        private var colorAlertDialog: AlertDialog? = null

        private var colorArray = IntArray(20)

        fun init(context: Context, stripNumber: Int) {
            initColors()

            val layoutInflater = LayoutInflater.from(context)
            colorPickerPalette = layoutInflater.inflate(R.layout.custom_picker, null) as ColorPickerPalette
            colorPickerPalette!!.init(colorArray.size, 5, object : ColorPickerSwatch.OnColorSelectedListener {
                override fun onColorSelected(color: Int) {

                    if (stripNumber == 1) {
                        ColorList.selection1 = colorArray.indexOf(color)
                        selectedColor1 = color
                    } else if (stripNumber == 2) {
                        ColorList.selection2 = colorArray.indexOf(color)
                        selectedColor2 = color
                    }

                    NetworkManager.addMessage(className, "StudyRoom|led_strip|strip=$stripNumber;${ColorList.getByIntValue(color)}")
                    colorAlertDialog!!.dismiss()
                }
            })

            if (stripNumber == 1) {
                colorPickerPalette!!.drawPalette(colorArray, selectedColor1)
            } else if (stripNumber == 2) {
                colorPickerPalette!!.drawPalette(colorArray, selectedColor2)
            }

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
                colorArray[index] = color.intValue
            }
        }
    }

}
