package mjaruijs.homecontrol.activities.lampsetup.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView

import mjaruijs.homecontrol.R
import mjaruijs.homecontrol.activities.lampsetup.data.AppItem
import mjaruijs.homecontrol.activities.lampsetup.data.AppList

class AppListAdapter(private val apps: AppList) : RecyclerView.Adapter<AppListAdapter.AppViewHolder>() {

    fun add(app: AppItem) {
        apps.add(app)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        return AppViewHolder(v)
    }

    override fun onBindViewHolder(holder: AppViewHolder, position: Int) {
        val appItem = apps[position]
        holder.appIcon.setImageDrawable(appItem.icon)
        holder.appName.text = appItem.name
        holder.addButton.tag = appItem.name
    }

    override fun getItemCount(): Int {
        return apps.size()
    }

    class AppViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var appIcon: ImageView = itemView.findViewById(R.id.app_icon)
        var appName: TextView = itemView.findViewById(R.id.app_text)
        var addButton: Button = itemView.findViewById(R.id.add_button)
    }
}
