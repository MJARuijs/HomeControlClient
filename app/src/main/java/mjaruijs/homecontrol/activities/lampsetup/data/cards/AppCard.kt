package mjaruijs.homecontrol.activities.lampsetup.data.cards

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.InputType
import android.view.View
import android.widget.EditText

import mjaruijs.homecontrol.R
import mjaruijs.homecontrol.activities.lampsetup.adapters.BlacklistAdapter
import mjaruijs.homecontrol.activities.lampsetup.adapters.SublistAdapter
import mjaruijs.homecontrol.colorpicker.Color

class AppCard : Card {

    var color: Color
    var blacklist: Blacklist
    var sublist: Sublist

    private lateinit var sublistAdapter: SublistAdapter
    private lateinit var sublistView: RecyclerView
    private lateinit var blacklistAdapter: BlacklistAdapter

    private var blacklistDialog: Dialog? = null
    private var sublistDialog: Dialog? = null
    private var inputDialog: Dialog? = null

    constructor(appName: String, appIcon: Drawable, color: Color) : super(appName, appIcon) {
        this.color = color
        blacklist = Blacklist()
        sublist = Sublist()
    }

    internal constructor(appName: String, appIcon: Drawable, color: Color, blacklist: Blacklist, sublist: Sublist) : super(appName, appIcon) {
        this.color = color
        this.blacklist = blacklist
        this.sublist = sublist
    }

    fun removeFromSublist(appName: String) {
        sublist.deleteCard(appName)
        sublistAdapter.notifyDataSetChanged()
    }

    fun removeFromBlackList(appName: String) {
        blacklist.deleteCard(appName)
        blacklistAdapter.notifyDataSetChanged()
    }

    fun setSubColor(subCardName: String, color: Int) {
        val states = arrayOf(IntArray(0))
        val colors = intArrayOf(color)
        val colorList = ColorStateList(states, colors)
        sublistView.findViewWithTag<View>(subCardName).backgroundTintList = colorList
    }

    private fun initSubDialog(context: Context) {
        sublistAdapter = SublistAdapter(sublist)

        sublistView = RecyclerView(context)

        val llm = LinearLayoutManager(context)
        sublistView.layoutManager = llm

        sublistView.adapter = sublistAdapter

        val builder = AlertDialog.Builder(context, R.style.Alert_Dialog_Dark)
        builder.setTitle("SubCards")
        builder.setView(sublistView)
                .setPositiveButton("+") { _, _ ->
                    showInputDialog(context, "sublist")
                    sublistAdapter.notifyDataSetChanged()
                }

        sublistDialog = builder.create()
    }

    private fun initBlackDialog(context: Context) {
        blacklistAdapter = BlacklistAdapter(blacklist)

        val blacklistView = RecyclerView(context)

        val llm = LinearLayoutManager(context)
        blacklistView.layoutManager = llm

        blacklistView.adapter = blacklistAdapter

        val builder = AlertDialog.Builder(context, R.style.Alert_Dialog_Dark)
        builder.setTitle("Blacklist")
        builder.setView(blacklistView)
                .setPositiveButton("+") { _, _ ->
                    showInputDialog(context, "blacklist")
                    blacklistAdapter.notifyDataSetChanged()
                }

        blacklistDialog = builder.create()
    }

    fun showSubCards(context: Context) {
        initSubDialog(context)
        sublistDialog?.show()
    }

    fun showBlacklist(context: Context) {
        initBlackDialog(context)
        blacklistDialog?.show()
    }

    private fun showInputDialog(context: Context, type: String) {
        val input = EditText(context)
        input.inputType = InputType.TYPE_CLASS_TEXT

        val builder = AlertDialog.Builder(context)
        builder.setTitle("Enter Name")
                .setView(input)
                .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
                .setOnDismissListener {
                    if (type == "sublist") {
                        sublistDialog?.show()
                    } else {
                        blacklistDialog?.show()
                    }
                }
        if (type == "sublist") {
            builder.setPositiveButton("Ok") { _, _ -> sublist.addCard(SubCard(input.text.toString(), Color(-1, 0f, 0f, 0f))) }
        } else if (type == "blacklist") {
            builder.setPositiveButton("Ok") { _, _ -> blacklist.addCard(BlackCard(input.text.toString())) }
        }

        inputDialog = builder.create()
        inputDialog?.show()
    }

    fun destroy() {
        sublistDialog?.dismiss()
        blacklistDialog?.dismiss()
        inputDialog?.dismiss()
    }
}