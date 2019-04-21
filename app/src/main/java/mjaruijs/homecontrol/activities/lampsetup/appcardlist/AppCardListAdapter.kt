package mjaruijs.homecontrol.activities.lampsetup.appcardlist

import android.animation.ObjectAnimator
import android.content.res.ColorStateList
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import kotlinx.android.synthetic.main.card_view.view.*
import mjaruijs.homecontrol.R
import mjaruijs.homecontrol.activities.OnSwipeTouchListener

class AppCardListAdapter(private val apps: ArrayList<AppCardItem> = ArrayList()) : RecyclerView.Adapter<AppCardListAdapter.AppCardViewHolder>() {

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

        holder.cardView.translationX = 0.0f
        holder.cardView.findViewById<ConstraintLayout>(R.id.card_view_layout).translationX = 0.0f
        holder.deleteBackground.translationX = 240.0f
        holder.deleteButton.translationX = 240.0f
        holder.deleteBackground.scaleX = 1.0f
        holder.appName.text = appCard.name
        holder.appIcon.setImageDrawable(appCard.icon)

        val stateList = arrayOf(intArrayOf(0))
        val colorList = intArrayOf(appCard.color)
        val colorStateList = ColorStateList(stateList, colorList)
        holder.appNotificationColor.backgroundTintList = colorStateList

        holder.cardView.setOnTouchListener(OnSwipeTouchListener())

        holder.deleteButton.setOnClickListener {
            animateDeletion(holder.cardView)
            holder.cardView.postDelayed({
                ObjectAnimator.ofFloat(holder.cardView, "translationX", -1440.0f).apply {
                    duration = 250
                }.start()
            }, 300)
            holder.cardView.postDelayed({
                apps.removeAt(position)
                notifyDataSetChanged()
            }, 600)
        }

        holder.deleteBackground.setOnClickListener {
            animateDeletion(holder.cardView)
            holder.cardView.postDelayed({
                ObjectAnimator.ofFloat(holder.cardView, "translationX", -1440.0f).apply {
                    duration = 250
                }.start()
            }, 300)
            holder.cardView.postDelayed({
                apps.removeAt(position)
                notifyDataSetChanged()
            }, 600)
        }
    }

    // TODO: Whenever a card is deleted, the cards below it move up to fill up its space. Animate this process.

    private fun animateDeletion(cardView: CardView) {
        ObjectAnimator.ofFloat(cardView.findViewById(R.id.delete_button), "translationX", -1440.0f).apply {
            duration = 1000
        }.start()
        ObjectAnimator.ofFloat(cardView.findViewById(R.id.card_view_layout), "translationX", -1680.0f).apply {
            duration = 1000
        }.start()
        ObjectAnimator.ofFloat(cardView.findViewById(R.id.delete_background), "scaleX", 12.96f).apply {
            duration = 1000
        }.start()
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