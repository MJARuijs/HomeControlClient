package mjaruijs.homecontrol.activities.lampsetup

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.lamp_activity.*
import mjaruijs.homecontrol.data.AppCardData
import mjaruijs.homecontrol.R
import mjaruijs.homecontrol.activities.dialogs.DialogButton
import mjaruijs.homecontrol.activities.dialogs.DialogButtonType
import mjaruijs.homecontrol.activities.dialogs.DialogConfig
import mjaruijs.homecontrol.activities.dialogs.DynamicAlertDialog
import mjaruijs.homecontrol.activities.lampsetup.appcardlist.AppCardItem
import mjaruijs.homecontrol.activities.lampsetup.appcardlist.AppCardListAdapter
import mjaruijs.homecontrol.activities.lampsetup.applist.AppListAdapter
import mjaruijs.homecontrol.activities.lampsetup.applist.AppListItem

class LampActivity : AppCompatActivity() {

    private lateinit var appCardListAdapter: AppCardListAdapter
    private lateinit var dynamicDialog: DynamicAlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.lamp_activity)

        setSupportActionBar(toolbar)

        val appListAdapter = AppListAdapter(this, ::onAddClick)
        val appListView = RecyclerView(this)
        appListView.adapter = appListAdapter
        appListView.layoutManager = LinearLayoutManager(this)

        dynamicDialog = DynamicAlertDialog(this)
        dynamicDialog.addConfig("app_list", DialogConfig("Apps", null, appListView, DialogButton(DialogButtonType.POSITIVE, "Ok") { dynamicDialog.dismiss() }))
        dynamicDialog.addConfig("duplication", DialogConfig("Duplication!", "This app is already in your list!", null, DialogButton(DialogButtonType.POSITIVE, "Ok") { dynamicDialog.dismiss() }))

        appCardListAdapter = AppCardListAdapter(dynamicDialog, AppCardData.getAppCards(this, dynamicDialog))
        recycle_view.adapter = appCardListAdapter
        recycle_view.layoutManager = LinearLayoutManager(this)

        fab.setOnClickListener {
            dynamicDialog.applyConfig("app_list")
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.lamp_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.delete_all -> {
                appCardListAdapter.deleteAll()
            }
            else -> super.onOptionsItemSelected(item)
        }
        return true
    }

    private fun onAddClick(appListItem: AppListItem) {
        if (appCardListAdapter.contains(appListItem.name)) {
            dynamicDialog.applyConfig("duplication")
        } else {
            appCardListAdapter.add(AppCardItem(dynamicDialog, appListItem))
            appCardListAdapter.notifyDataSetChanged()
        }
    }

    override fun onPause() {
        super.onPause()
        dynamicDialog.clear()
        AppCardData.write(this, appCardListAdapter.apps)
    }

//    internal inner class ColorPicker {
//
//        private var colorPickerPalette: ColorPickerPalette? = null
//        private var colors = IntArray(20)
//
//        var colorAlertDialog: AlertDialog? = null
//        var initialized = false
//
//        @SuppressLint("InflateParams")
//        fun init(context: Context, subCardName: String?) {
//            initialized = true
//
//            initColors()
//
//            val layoutInflater = LayoutInflater.from(context)
//            colorPickerPalette = layoutInflater.inflate(R.layout.custom_picker, null) as ColorPickerPalette
//            colorPickerPalette!!.init(colors.size, 5, object : ColorPickerSwatch.OnColorSelectedListener {
//                override fun onColorSelected(color: Int) {
//                    if (subCardName == null) {
////                        selectedCard.color = ColorList.getByIntValue(color)
//                        val states = arrayOf(IntArray(0))
//                        val colors = intArrayOf(color)
//                        val colorList = ColorStateList(states, colors)
//                        recycle_view.findViewWithTag<View>(selectedCard).backgroundTintList = colorList
//                    } else {
////                        val subCard = selectedCard.sublist[subCardName]
//
////                        if (subCard != null) {
////                            subCard.color.intValue = color
////                            selectedCard.setSubColor(subCardName, color)
////                        }
//                    }
//                    colorAlertDialog!!.dismiss()
//                }
//            })
//
//            if (subCardName == null) {
////                colorPickerPalette!!.drawPalette(colors, selectedCard.color.intValue)
//            } else {
////                colorPickerPalette!!.drawPalette(colors, selectedCard.sublist[subCardName]!!.color.intValue)
//            }
//
//            colorAlertDialog = AlertDialog.Builder(context, R.style.Alert_Dialog_Dark)
//                    .setTitle(R.string.color_picker_default_title)
//                    .setView(colorPickerPalette)
//                    .create()
//        }
//
//        fun show() {
//            colorAlertDialog!!.show()
//        }
//
//        private fun initColors() {
//            for (i in 0 until ColorList.colors.size) {
//                val color = ColorList.colors[i]
//                colors[i] = color.intValue
//            }
//        }

//    }
}