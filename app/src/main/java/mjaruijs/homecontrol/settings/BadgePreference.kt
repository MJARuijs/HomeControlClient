package mjaruijs.homecontrol.settings

import android.content.Context
import android.graphics.drawable.Drawable
import android.preference.Preference
import android.support.v7.content.res.AppCompatResources
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import mjaruijs.homecontrol.R

class BadgePreference : Preference {
    private var badge: Drawable? = null
    private lateinit var titleView: TextView

    constructor(context: Context) : super(context)

//    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
//        val a = context.theme.obtainStyledAttributes(
//                attrs,
//                R.styleable.PreferencesBadge,
//                0, 0)
//
//        try {
//            // Resources$NotFoundException if vector image
//            // badge = a.getDrawable(R.styleable.PreferencesBadge_badge)
//            val drawableResId = a.getResourceId(R.styleable.badge, -1);
//            badge = AppCompatResources.getDrawable(context, drawableResId)
//        }
//        finally {
//            a.recycle()
//        }
//    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle)

    override fun onBindView(view: View) {
        super.onBindView(view)
        titleView = view.findViewById(android.R.id.title) as TextView

        if (badge != null) {
            setBadge(badge)
        }
    }

    fun setBadge(badge: Drawable?) {
        this.badge = badge
        titleView.compoundDrawablePadding = 20
        titleView.setCompoundDrawablesWithIntrinsicBounds(null, null, badge, null)
    }
}