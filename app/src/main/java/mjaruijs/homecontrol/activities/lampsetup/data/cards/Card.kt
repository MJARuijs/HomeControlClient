package mjaruijs.homecontrol.activities.lampsetup.data.cards

import android.graphics.drawable.Drawable

abstract class Card(val appName: String, val appIcon: Drawable?, var selected: Boolean = false)
