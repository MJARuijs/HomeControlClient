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
    private var currentConfig: DialogConfig? = null

    init {
        dialog = builder.create()
    }

    fun addConfig(name: String, config: DialogConfig) {
        configs.putIfAbsent(name, config)
    }

    fun getConfig(name: String) = configs[name]

    fun replaceConfig(name: String, config: DialogConfig) {
        configs[name] = config
    }

    fun removeConfig(name: String) {
        configs.remove(name)
    }

    fun clear() {
        configs.clear()
    }

    fun applyConfig(name: String) {
        val config = configs[name]
        if (config == null) {
            println("DOES NOT CONTAIN $name")
            return
        }
        setButtons(*config.buttons)
        setMessage(config.message)
        setTitle(config.title)
        setView(config.view)

        currentConfig = config
        dialog = builder.create()
        dialog.setOnCancelListener {
            dismiss()
        }

        show()
    }

    private fun show() {
        if (currentConfig != null) {
            for (function in currentConfig!!.onShow) {
                function.invoke()
            }
        }

        dialog.show()
    }

    fun dismiss() {
        if (currentConfig != null) {
            for (function in currentConfig!!.onDismiss) {
                function.invoke()
            }
        }

        dialog.dismiss()
    }

    private fun setButtons(vararg buttons: DialogButton) {
        builder.setPositiveButton("") { _, _ -> }
        builder.setNeutralButton("") { _, _ -> }
        builder.setNegativeButton("") { _, _ -> }

        for (button in buttons) {
            when (button.type) {
                DialogButtonType.POSITIVE -> builder.setPositiveButton(button.text) { _, _ -> button.onClick.invoke() }
                DialogButtonType.NEUTRAL -> builder.setNeutralButton(button.text) { _, _ -> button.onClick.invoke() }
                DialogButtonType.NEGATIVE -> builder.setNegativeButton(button.text) { _, _ -> button.onClick.invoke() }
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