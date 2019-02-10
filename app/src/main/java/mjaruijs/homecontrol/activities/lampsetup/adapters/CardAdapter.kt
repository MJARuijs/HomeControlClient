package mjaruijs.homecontrol.activities.lampsetup.adapters

import android.content.res.ColorStateList
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView

import mjaruijs.homecontrol.R
import mjaruijs.homecontrol.activities.lampsetup.data.cards.AppCardList

class CardAdapter(private val cards: AppCardList) : RecyclerView.Adapter<CardAdapter.CardViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.card_view, parent, false)
        return CardViewHolder(v)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val card = cards[position]
        val tag = cards[position].appName

        val nameTag = tag + "_Name"
        val iconTag = tag + "_Icon"
        val deleteBtnTag = tag + "_Del_Btn"
        val deleteBackGrdTag = tag + "_Del_Backgrd"

        holder.appName.text = card.appName
        holder.appIcon.setImageDrawable(card.appIcon)
        holder.appNotificationColor.tag = card.appName

        val states = arrayOf(IntArray(0))
        val colors = intArrayOf(card.color.intValue)
        val colorList = ColorStateList(states, colors)
        holder.appNotificationColor.backgroundTintList = colorList

        holder.deleteButton.tag = card.appName
        holder.appName.tag = nameTag
        holder.appIcon.tag = iconTag
        holder.deleteBackground.tag = deleteBackGrdTag
        holder.deleteButton.tag = deleteBtnTag
        holder.deleteButton.setBackgroundResource(R.drawable.delete_icon)
        holder.subCardsButton.tag = card.appName
        holder.blacklistButton.tag = card.appName

        holder.cv.setOnLongClickListener {
            cards.getByName(tag)!!.selected = true
            holder.deleteBackground.visibility = View.VISIBLE
            holder.deleteButton.visibility = View.VISIBLE
            holder.appNotificationColor.visibility = View.INVISIBLE
            holder.appName.visibility = View.INVISIBLE
            true
        }

    }

    override fun getItemCount(): Int {
        return cards.size()
    }

    class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var cv: CardView = itemView.findViewById(R.id.card_view)
        var deleteButton: Button = itemView.findViewById(R.id.delete_button)
        var deleteBackground: ImageView = itemView.findViewById(R.id.delete_background)
        var appName: TextView = itemView.findViewById(R.id.app_name)
        var appIcon: ImageView = itemView.findViewById(R.id.app_icon)
        var appNotificationColor: Button = itemView.findViewById(R.id.app_notification_color)
        var subCardsButton: Button = itemView.findViewById(R.id.subCards_button)
        var blacklistButton: Button = itemView.findViewById(R.id.blacklist_button)
    }
}
