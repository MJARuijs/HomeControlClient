package mjaruijs.homecontrol.activities.lampsetup.applist

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import kotlinx.android.synthetic.main.app_list_item.view.*
import mjaruijs.homecontrol.InstalledAppsCache
import mjaruijs.homecontrol.R

class AppListAdapter(context: Context, private val onAddButtonClick: (AppListItem) -> Unit) : RecyclerView.Adapter<AppListAdapter.AppListViewHolder>() {

    private val apps = ArrayList<AppListItem>()

    init {
        InstalledAppsCache.get(context).forEach { pInfo ->
            apps.add(AppListItem(pInfo.name, pInfo.icon))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppListViewHolder {
        return AppListViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.app_list_item, parent, false))
    }

    override fun getItemCount(): Int {
        return apps.size
    }

    override fun onBindViewHolder(holder: AppListViewHolder, position: Int) {
        val appItem = apps[position]
        holder.name.text = appItem.name
        holder.icon.setImageDrawable(appItem.icon)
        holder.addButton.setOnClickListener { onAddButtonClick.invoke(appItem) }
    }

    inner class AppListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var icon: ImageView = itemView.app_item_icon
        var name: TextView = itemView.app_item_text
        var addButton: Button = itemView.app_item_button
    }

}