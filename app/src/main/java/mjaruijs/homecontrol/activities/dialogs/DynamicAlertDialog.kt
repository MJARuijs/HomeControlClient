package mjaruijs.homecontrol.activities.dialogs

import android.app.AlertDialog
import android.content.Context
import android.view.View
import android.view.ViewGroup
import mjaruijs.homecontrol.R

class DynamicAlertDialog(val context: Context) {

    private var dialog: AlertDialog
    private var builder = AlertDialog.Builder(context, R.style.Alert_Dialog_Dark)

    private val configs = HashMap<String, DialogConfig>()

    init {
        dialog = builder.create()
    }

    fun addConfig(name: String, config: DialogConfig) {
        configs.putIfAbsent(name, config)
    }

    fun applyConfig(name: String) {
        val config = configs[name] ?: return

        setButtons(*config.buttons)
        setMessage(config.message)
        setTitle(config.title)
        setView(config.view)

        dialog = builder.create()
    }

    fun show() {
        dialog.show()
    }

    fun dismiss() {
        dialog.dismiss()
    }

    private fun setButtons(vararg buttons: DialogButton) {
        for (button in buttons) {
            when (button.type) {
                DialogButtonType.POSITIVE -> builder.setPositiveButton(button.text) { _, _ -> button.onClick }
                DialogButtonType.NEUTRAL -> builder.setNeutralButton(button.text) { _, _ -> button.onClick }
                DialogButtonType.NEGATIVE -> builder.setNegativeButton(button.text) { _, _ -> button.onClick }
            }
        }
    }

    private fun setMessage(message: String?) {
        builder.setMessage(message)
    }

    private fun setTitle(title: String) {
        builder.setTitle(title)
    }

    private fun setView(view: View?) {
        if (view?.parent != null) {
            (view.parent as ViewGroup).removeAllViews()
        } else {
            if (dialog.listView?.parent != null) {
                (dialog.listView?.parent as ViewGroup).removeAllViews()
            }
        }
        builder.setView(view)
    }

}