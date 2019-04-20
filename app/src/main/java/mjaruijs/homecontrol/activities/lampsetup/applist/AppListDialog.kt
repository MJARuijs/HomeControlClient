package mjaruijs.homecontrol.activities.lampsetup.applist

import android.app.AlertDialog
import android.content.Context
import android.view.View
import mjaruijs.homecontrol.R

class AppListDialog(context: Context, title: String, view: View) {

    private val dialog: AlertDialog

    init {
        val builder = AlertDialog.Builder(context, R.style.Alert_Dialog_Dark)
        builder.setTitle(title)
                .setView(view)
                .setPositiveButton("Ok") { _, _ -> }
        dialog = builder.create()
    }

    fun show() {
        dialog.show()
    }

    fun hide() {
        dialog.dismiss()
    }
}