package mjaruijs.homecontrol.activities.dialogs

import android.content.DialogInterface.*

enum class DialogButtonType(val id: Int) {

    NEGATIVE(BUTTON_NEGATIVE),
    NEUTRAL(BUTTON_NEUTRAL),
    POSITIVE(BUTTON_POSITIVE)

}