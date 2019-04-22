package mjaruijs.homecontrol.activities.lampsetup.blacklist

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import kotlinx.android.synthetic.main.blacklist_item.view.*
import mjaruijs.homecontrol.R

class BlackListAdapter(private val blackList: ArrayList<BlackListItem> = ArrayList()) : RecyclerView.Adapter<BlackListAdapter.BlackListItemViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BlackListItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.blacklist_item, parent, false)
        return BlackListItemViewHolder(view)
    }

    override fun getItemCount(): Int {
        return blackList.size
    }

    override fun onBindViewHolder(holder: BlackListItemViewHolder, position: Int) {
        val blackListItem = blackList[position]

        holder.textView.text = blackListItem.name
        holder.deleteButton.setOnClickListener {
            blackList.removeAt(position)
            notifyDataSetChanged()
        }
    }

    operator fun plusAssign(blackListItem: BlackListItem) {
        blackList += blackListItem
    }

    override fun toString(): String {
        return blackList.joinToString(";", "", "", -1, "", null)
    }

    companion object {
        fun parse(string: String): ArrayList<BlackListItem> {
            val blackList = ArrayList<BlackListItem>()
            val values = string.split(';')
            values.forEach { value ->
                if (value.isNotBlank()) {
                    blackList += BlackListItem.parse(value)
                }
            }
            return blackList
        }
    }

    inner class BlackListItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var textView: TextView = view.blacklist_text
        var deleteButton: Button = view.blacklist_delete_button
    }

}