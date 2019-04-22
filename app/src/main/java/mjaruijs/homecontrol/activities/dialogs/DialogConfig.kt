package mjaruijs.homecontrol.activities.dialogs

import android.view.View

class DialogConfig(val title: String,
                   val message: String?,
                   val view: View?,
                   vararg val buttons: DialogButton,
                   val onShow: ArrayList<() -> Unit> = ArrayList(),
                   val onDismiss: ArrayList<() -> Unit> = ArrayList())