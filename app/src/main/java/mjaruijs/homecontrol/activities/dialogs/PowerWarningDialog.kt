package mjaruijs.homecontrol.activities.dialogs

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface

object PowerWarningDialog {

    fun getDialog(context: Context, onClickListener: DialogInterface.OnClickListener): AlertDialog {
        return AlertDialog.Builder(context)
                .setTitle("Warning!")
                .setMessage("Your PC is still running! " +
                        "Turning the power off now might result in the loss of any unsaved data. " +
                        "Are you sure you want to continue?")
                .setNegativeButton("Cancel", onClickListener)
                .setPositiveButton("Turn off anyway", onClickListener).create()
    }

}