package mjaruijs.homecontrol.activities.lampsetup.adapters

import android.content.res.ColorStateList
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView

import mjaruijs.homecontrol.R
import mjaruijs.homecontrol.activities.lampsetup.data.cards.Sublist

class SublistAdapter(private val sublist: Sublist) : RecyclerView.Adapter<SublistAdapter.SublistHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SublistHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.subcard_item, parent, false)
        return SublistHolder(view)
    }

    override fun onBindViewHolder(holder: SublistHolder, position: Int) {
        val card = sublist[position]
        val tag = sublist[position].item

        val deleteBtnTag = tag + "_Del_Btn"
        val states = arrayOf(IntArray(0))
        val colors = intArrayOf(card.color.intValue)
        val colorList = ColorStateList(states, colors)

        holder.notificationColor.backgroundTintList = colorList
        holder.notificationColor.tag = tag
        holder.item.text = tag
        holder.deleteButton.tag = deleteBtnTag
    }

    override fun getItemCount(): Int {
        return sublist.size()
    }

    class SublistHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var notificationColor: Button = itemView.findViewById(R.id.sub_notification_color)
        var item: TextView = itemView.findViewById(R.id.sub_text)
        var deleteButton: Button = itemView.findViewById(R.id.sub_delete_button)
    }

}
