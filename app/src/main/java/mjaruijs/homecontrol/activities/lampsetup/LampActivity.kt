package mjaruijs.homecontrol.activities.lampsetup

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Environment
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import kotlinx.android.synthetic.main.content_main.*
import mjaruijs.homecontrol.R
import mjaruijs.homecontrol.activities.lampsetup.adapters.AppListAdapter
import mjaruijs.homecontrol.activities.lampsetup.adapters.CardAdapter
import mjaruijs.homecontrol.activities.lampsetup.data.AppItem
import mjaruijs.homecontrol.activities.lampsetup.data.AppList
import mjaruijs.homecontrol.activities.lampsetup.data.Data
import mjaruijs.homecontrol.activities.lampsetup.data.PackageInfo
import mjaruijs.homecontrol.activities.lampsetup.data.cards.AppCard
import mjaruijs.homecontrol.activities.lampsetup.data.cards.AppCardList
import mjaruijs.homecontrol.colorpicker.ColorList
import mjaruijs.homecontrol.colorpicker.ColorPickerPalette
import mjaruijs.homecontrol.colorpicker.ColorPickerSwatch
import java.util.ArrayList
import kotlin.collections.HashMap
import kotlin.collections.List
import kotlin.collections.indices
import kotlin.collections.set

class LampActivity : AppCompatActivity() {

    private lateinit var cards: AppCardList
    private var selectedCard: AppCard? = null

    private var deleteAllCardsDialog: AlertDialog? = null
    private var deleteMultipleCardsDialog: AlertDialog? = null
    private lateinit var duplicationDialog: AlertDialog
    private lateinit var appListDialog: Dialog

    private val colorPicker = ColorPicker()
    private val iconMap = HashMap<String, Drawable>()

    private lateinit var appCardAdapter: CardAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.lamp_activity)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        recycle_view.layoutManager = LinearLayoutManager(applicationContext)

        var applicationPackages = ArrayList<PackageInfo>()
        try {
            applicationPackages = getInstalledApps()
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

        val file = Environment.getExternalStorageDirectory()
        Data.initialize(file, iconMap)

        cards = Data.getCards(file, iconMap)!!

        appCardAdapter = CardAdapter(cards)
        recycle_view.adapter = appCardAdapter

        val appList = AppList()

        for (i in applicationPackages.indices) {
            appList.add(AppItem(applicationPackages[i].appName, applicationPackages[i].icon!!))
        }

        appList.sort()

        val appListAdapter = AppListAdapter(appList)

        val appListView = RecyclerView(this)

        val llm = LinearLayoutManager(this)

        appListView.layoutManager = llm
        appListView.adapter = appListAdapter

        val builder = AlertDialog.Builder(this, R.style.Alert_Dialog_Dark)
        builder.setTitle("Apps")
                .setView(appListView)
                .setPositiveButton("Ok") { _, _ -> }

        appListDialog = builder.create()

        duplicationDialog = AlertDialog.Builder(this, R.style.Alert_Dialog_Dark)
                .setTitle("Duplication!")
                .setMessage("This app is already in your list!")
                .setPositiveButton("Ok") { dialog, _ ->
                    dialog.dismiss()
                }.create()

        fab.setOnClickListener {
            appListDialog.show()
        }

//        add_button.setOnClickListener {
//            if (cards.contains(add_button.tag.toString())) {
//                duplicationDialog.show()
//            } else {
//                val appName = add_button.tag.toString()
//                val icon = iconMap.getValue(appName)
//
//                cards.addCard(AppCard(appName, icon, Color(19, 255f, 255f, 255f)))
//                appCardAdapter.notifyDataSetChanged()
//            }
//        }

    }

    fun onClickDeleteSubCard(view: View) {
        selectedCard!!.removeFromSublist(view.tag.toString())
    }

    fun onClickDeleteBlackCard(view: View) {
        selectedCard!!.removeFromBlackList(view.tag.toString())
    }

    fun onClickDeleteCard(v: View) {
        val tag = v.tag.toString().replace("_Del_Btn", "")
        val nameTag = tag + "_Name"
        val iconTag = tag + "_Icon"
        val deleteBtnTag = tag + "_Del_Btn"
        val deleteBackGrdTag = tag + "_Del_Backgrd"

        if (cards.multipleSelected()) {
            val selectedCards = cards.selectedCards
            deleteMultipleCardsDialog = AlertDialog.Builder(this, R.style.Alert_Dialog_Dark)
                    .setTitle("Multiple Items Selected!")
                    .setMessage("Do you want to delete all selected items?")
                    .setPositiveButton("Ok") { dialog, _ ->
                        deselectCards(selectedCards, true)
                        cards.deselectCards()
                        appCardAdapter.notifyDataSetChanged()
                        dialog.dismiss()
                    }.setNegativeButton("Cancel") { dialog, _ ->
                        deselectCards(selectedCards, false)
                        cards.deselectCards()
                        appCardAdapter.notifyDataSetChanged()
                        dialog.dismiss()
                    }
                    .create()
            deleteMultipleCardsDialog!!.show()
        } else {
            recycle_view.findViewWithTag<View>(tag).visibility = View.VISIBLE
            recycle_view.findViewWithTag<View>(nameTag).visibility = View.VISIBLE
            recycle_view.findViewWithTag<View>(iconTag).visibility = View.VISIBLE
            recycle_view.findViewWithTag<View>(deleteBtnTag).visibility = View.INVISIBLE
            recycle_view.findViewWithTag<View>(deleteBackGrdTag).visibility = View.INVISIBLE
            cards.deleteCard(v.tag.toString())
            cards.deselectCards()
            appCardAdapter.notifyDataSetChanged()
        }

    }

    private fun deselectCards(selectedCards: List<AppCard>, deleteCard: Boolean) {
        for (card in selectedCards) {
            card.selected = false
            val tag = card.appName
            val nameTag = card.appName + "_Name"
            val iconTag = card.appName + "_Icon"
            val deleteBtnTag = card.appName + "_Del_Btn"
            val deleteBackGrdTag = card.appName + "_Del_Backgrd"
            recycle_view.findViewWithTag<View>(tag).visibility = View.VISIBLE
            recycle_view.findViewWithTag<View>(nameTag).visibility = View.VISIBLE
            recycle_view.findViewWithTag<View>(iconTag).visibility = View.VISIBLE
            recycle_view.findViewWithTag<View>(deleteBtnTag).visibility = View.INVISIBLE
            recycle_view.findViewWithTag<View>(deleteBackGrdTag).visibility = View.INVISIBLE

            if (deleteCard) {
                cards.deleteCard(card.appName)
            }
        }
    }

    fun onClickSubColorButton(view: View) {
        colorPicker.init(this, selectedCard!!, view.tag.toString())
        colorPicker.show()
    }

    fun onClickColorButton(v: View) {
        selectedCard = cards.getByName(v.tag.toString()) ?: return
        colorPicker.init(this, selectedCard!!, null)
        colorPicker.show()
    }

    fun showSubCards(view: View) {
        selectedCard = cards.getByName(view.tag.toString())
        selectedCard!!.showSubCards(this)
    }

    fun showBlacklist(view: View) {
        selectedCard = cards.getByName(view.tag.toString())
        selectedCard!!.showBlacklist(this)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.delete_all -> {
                showWarningDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showWarningDialog() {
        val selectedCards = cards.selectedCards
        deleteAllCardsDialog = AlertDialog.Builder(this, R.style.Alert_Dialog_Dark)
                .setTitle("Warning!")
                .setMessage("Are you sure you want to delete all Cards?")
                .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
                .setPositiveButton("Ok") { dialog, _ ->
                    for (card in selectedCards) {
                        card.selected = false
                        val tag = card.appName
                        val nameTag = card.appName + "_Name"
                        val iconTag = card.appName + "_Icon"
                        val deleteBtnTag = card.appName + "_Del_Btn"
                        val deleteBackGrdTag = card.appName + "_Del_Backgrd"
                        recycle_view.findViewWithTag<View>(tag).visibility = View.VISIBLE
                        recycle_view.findViewWithTag<View>(nameTag).visibility = View.VISIBLE
                        recycle_view.findViewWithTag<View>(iconTag).visibility = View.VISIBLE
                        recycle_view.findViewWithTag<View>(deleteBtnTag).visibility = View.INVISIBLE
                        recycle_view.findViewWithTag<View>(deleteBackGrdTag).visibility = View.INVISIBLE
                    }
                    cards.deselectCards()
                    cards.clear()
                    appCardAdapter.notifyDataSetChanged()
                    dialog.dismiss()
                }
                .create()
        deleteAllCardsDialog!!.show()
    }

    public override fun onResume() {
        super.onResume()
    }

    public override fun onDestroy() {
        cleanUp()
        super.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        cleanUp()
        super.onSaveInstanceState(outState)
    }

    private fun cleanUp() {
        appListDialog.dismiss()
        duplicationDialog.dismiss()

        if (deleteAllCardsDialog != null) {
            deleteAllCardsDialog!!.dismiss()
        }

        if (deleteMultipleCardsDialog != null) {
            deleteMultipleCardsDialog!!.dismiss()
        }

        if (selectedCard != null) {
            selectedCard!!.destroy()
        }

        closeOptionsMenu()
        Data.writeToFile()
        if (colorPicker.initialized && colorPicker.colorAlertDialog!!.isShowing) {
            colorPicker.colorAlertDialog!!.dismiss()
        }

    }

    private fun getInstalledApps(): ArrayList<PackageInfo> {
        val res = ArrayList<PackageInfo>()
        val flags = PackageManager.GET_META_DATA or PackageManager.GET_SHARED_LIBRARY_FILES

        val pm = packageManager

        val applications = pm.getInstalledPackages(flags)

        for (appInfo in applications) {
            if ((((appInfo.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 1)
                            || (appInfo.applicationInfo.loadLabel(packageManager).toString() == "Gmail")
                            || (appInfo.applicationInfo.loadLabel(packageManager).toString() == "YouTube")
                            || (appInfo.packageName.contains("facebook")) && !appInfo.applicationInfo.loadLabel(packageManager).toString().contains("App")
                            || (appInfo.applicationInfo.loadLabel(packageManager).toString().contains("Messenger")))) {
                val newInfo = PackageInfo()
                newInfo.appName = appInfo.applicationInfo.loadLabel(packageManager).toString()
                newInfo.icon = appInfo.applicationInfo.loadIcon(packageManager)
                res.add(newInfo)
                iconMap[newInfo.appName] = newInfo.icon ?: continue
            }
        }
        return res
    }

    internal inner class ColorPicker {

        private var colorPickerPalette: ColorPickerPalette? = null
        private var colors = IntArray(20)

        var colorAlertDialog: AlertDialog? = null
        var initialized = false

        @SuppressLint("InflateParams")
        fun init(context: Context, selectedCard: AppCard, subCardName: String?) {
            initialized = true

            initColors()

            val layoutInflater = LayoutInflater.from(context)
            colorPickerPalette = layoutInflater.inflate(R.layout.custom_picker, null) as ColorPickerPalette
            colorPickerPalette!!.init(colors.size, 5, object : ColorPickerSwatch.OnColorSelectedListener {
                override fun onColorSelected(color: Int) {
                    if (subCardName == null) {
                        selectedCard.color = ColorList.getByIntValue(color)
                        val states = arrayOf(IntArray(0))
                        val colors = intArrayOf(color)
                        val colorList = ColorStateList(states, colors)
                        recycle_view.findViewWithTag<View>(selectedCard.appName).backgroundTintList = colorList
                    } else {
                        val subCard = selectedCard.sublist[subCardName]

                        if (subCard != null) {
                            subCard.color.intValue = color
                            selectedCard.setSubColor(subCardName, color)
                        }
                    }
                    colorAlertDialog!!.dismiss()
                }
            })

            if (subCardName == null) {
                colorPickerPalette!!.drawPalette(colors, selectedCard.color.intValue)
            } else {
                colorPickerPalette!!.drawPalette(colors, selectedCard.sublist[subCardName]!!.color.intValue)
            }

            colorAlertDialog = AlertDialog.Builder(context, R.style.Alert_Dialog_Dark)
                    .setTitle(R.string.color_picker_default_title)
                    .setView(colorPickerPalette)
                    .create()
        }

        fun show() {
            colorAlertDialog!!.show()
        }

        private fun initColors() {
            for (i in 0 until ColorList.colors.size) {
                val color = ColorList.colors[i]
                colors[i] = color.intValue
            }
        }

    }
}