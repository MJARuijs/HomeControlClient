package mjaruijs.homecontrol.activities.lampsetup.subcardlist

import android.content.res.ColorStateList
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import kotlinx.android.synthetic.main.subcard_item.view.*
import mjaruijs.homecontrol.R
import mjaruijs.homecontrol.colorpicker.ColorPickerView
import mjaruijs.homecontrol.activities.dialogs.DynamicAlertDialog
import mjaruijs.homecontrol.colorpicker.ColorPickerSwatch

class SubCardAdapter(private val dynamicDialog: DynamicAlertDialog, private val colorPickerView: ColorPickerView, val subCards: ArrayList<SubCardItem> = ArrayList()) : RecyclerView.Adapter<SubCardAdapter.SubCardViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubCardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.subcard_item, parent, false)
        return SubCardViewHolder(view)
    }

    override fun getItemCount(): Int {
        return subCards.size
    }

    override fun onBindViewHolder(holder: SubCardViewHolder, position: Int) {
        val subCard = subCards[position]

        holder.itemTextView.text = subCard.name
        val stateList = arrayOf(intArrayOf(0))
        val colorList = intArrayOf(subCard.color)
        val colorStateList = ColorStateList(stateList, colorList)
        holder.notificationColor.backgroundTintList = colorStateList
        holder.deleteButton.setOnClickListener {
            subCards.removeAt(position)
            notifyDataSetChanged()
        }

        holder.notificationColor.setOnClickListener {
            colorPickerView.setOnClickListener(object : ColorPickerSwatch.OnColorSelectedListener {
                override fun onColorSelected(color: Int) {
                    val state = arrayOf(intArrayOf(0))
                    val colorArray = intArrayOf(color)
                    val colorState = ColorStateList(state, colorArray)
                    holder.notificationColor.backgroundTintList = colorState
                    subCard.color = color
                    println(color)
                    dynamicDialog.dismiss()
                }
            })

            dynamicDialog.applyConfig("color_picker")
        }

    }

    operator fun plusAssign(subCard: SubCardItem) {
        if (subCards.none { card -> card.name == subCard.name }) {
            subCards += subCard
        }
    }

    override fun toString(): String {
        return subCards.joinToString(";", "", "", -1, "", null)
    }

    companion object {
        fun parse(string: String): ArrayList<SubCardItem> {
            val subCards = ArrayList<SubCardItem>()

            val values = string.split(';')
            println(string)
            println(values.size)
            values.forEach { value ->
                if (value.isNotBlank()) {
                    subCards += SubCardItem.parse(value)
                }
            }

            return subCards
        }
    }

    inner class SubCardViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var notificationColor: Button = view.sub_notification_color
        var itemTextView: TextView = view.sub_text
        var deleteButton: Button = view.sub_delete_button
    }

}