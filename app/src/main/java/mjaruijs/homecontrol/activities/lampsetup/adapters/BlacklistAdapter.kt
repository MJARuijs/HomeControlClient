package mjaruijs.homecontrol.activities.lampsetup.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import mjaruijs.homecontrol.R

import mjaruijs.homecontrol.activities.lampsetup.data.cards.Blacklist

class BlacklistAdapter(private val cards: Blacklist) : RecyclerView.Adapter<BlacklistAdapter.BlacklistHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BlacklistHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.blacklist_item, parent, false)
        return BlacklistHolder(view)
    }

    override fun onBindViewHolder(holder: BlacklistHolder, position: Int) {
        val tag = cards[position].appName
        val deleteBtnTag = tag + "_Del_Btn"

        holder.itemName.text = cards.get(position).item
        holder.deleteButton.tag = deleteBtnTag
    }

    override fun getItemCount(): Int {
        return cards.size()
    }

    class BlacklistHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var itemName: TextView = itemView.findViewById(R.id.blacklist_text)
        var deleteButton: Button = itemView.findViewById(R.id.blacklist_delete_button)
    }
}
