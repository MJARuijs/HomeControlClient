package mjaruijs.homecontrol.activities.lampsetup.appcardlist

import android.content.Context
import android.content.res.ColorStateList
import android.support.constraint.ConstraintSet
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import kotlinx.android.synthetic.main.card_view.view.*
import mjaruijs.homecontrol.R
import mjaruijs.homecontrol.activities.OnSwipeTouchListener

class AppCardListAdapter(private val apps: ArrayList<AppCardItem> = ArrayList()) : RecyclerView.Adapter<AppCardListAdapter.AppCardViewHolder>() {



    init {


    }

    fun add(appCard: AppCardItem) {
        apps += appCard
    }

    fun contains(name: String): Boolean {
        return apps.any { card -> card.name == name }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppCardViewHolder {
        return AppCardViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.card_view, parent, false))
    }

    override fun getItemCount(): Int {
        return apps.size
    }

    override fun onBindViewHolder(holder: AppCardViewHolder, position: Int) {
        val appCard = apps[position]

        holder.appName.text = appCard.name
        holder.appIcon.setImageDrawable(appCard.icon)

        val stateList = arrayOf(intArrayOf(0))
        val colorList = intArrayOf(appCard.color)
        val colorStateList = ColorStateList(stateList, colorList)
        holder.appNotificationColor.backgroundTintList = colorStateList

        holder.cardView.setOnTouchListener(OnSwipeTouchListener())
    }

    inner class AppCardViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var cardView: CardView = view.card_view
        var deleteButton: Button = view.delete_button
        var deleteBackground: ImageView = view.delete_background
        var appName: TextView = view.app_name
        var appIcon: ImageView = view.app_icon
        var appNotificationColor: Button = view.app_notification_color
        var subCardsButton: Button = view.subCards_button
        var blacklistButton: Button = view.blacklist_button
    }
}