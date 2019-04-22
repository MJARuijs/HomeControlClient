package mjaruijs.homecontrol.activities.lampsetup.appcardlist

import android.animation.ObjectAnimator
import android.content.Context
import android.content.res.ColorStateList
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.inputmethod.InputMethodManager
import android.view.inputmethod.InputMethodManager.SHOW_FORCED
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import kotlinx.android.synthetic.main.card_view.view.*
import mjaruijs.homecontrol.R
import mjaruijs.homecontrol.activities.OnSwipeTouchListener
import mjaruijs.homecontrol.activities.dialogs.DialogButton
import mjaruijs.homecontrol.activities.dialogs.DialogButtonType
import mjaruijs.homecontrol.activities.dialogs.DialogConfig
import mjaruijs.homecontrol.activities.dialogs.DynamicAlertDialog
import mjaruijs.homecontrol.activities.lampsetup.blacklist.BlackListItem
import mjaruijs.homecontrol.activities.lampsetup.subcardlist.SubCardItem

class AppCardListAdapter(private val dynamicDialog: DynamicAlertDialog, val apps: ArrayList<AppCardItem> = ArrayList()) : RecyclerView.Adapter<AppCardListAdapter.AppCardViewHolder>() {

    private var selectedDialog = ""
    private val inputManager = dynamicDialog.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    private val inputView = EditText(dynamicDialog.context)

    init {
        inputView.inputType = InputType.TYPE_CLASS_TEXT

        dynamicDialog.addConfig("input", DialogConfig("Enter Name", null, inputView,
                DialogButton(DialogButtonType.NEGATIVE, "Cancel") {
                    dynamicDialog.dismiss()
                    inputView.text.clear()
                    dynamicDialog.applyConfig(selectedDialog)
                },
                DialogButton(DialogButtonType.POSITIVE, "Ok") {
                    val appCard = apps.find { app -> selectedDialog.contains(app.name) } ?: return@DialogButton
                    if (selectedDialog.startsWith("sub_cards")) {
                        appCard.subListAdapter += SubCardItem(inputView.text.toString())
                    } else if (selectedDialog.startsWith("black_list")) {
                        appCard.blackListAdapter += BlackListItem(inputView.text.toString())
                    }

                    dynamicDialog.dismiss()
                    inputView.text.clear()
                    dynamicDialog.applyConfig(selectedDialog)
                },
                onShow = arrayListOf(::showKeyboard),
                onDismiss = arrayListOf(::hideKeyboard)
        ))

        notifyDataSetChanged()
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

        dynamicDialog.addConfig("sub_cards_${appCard.name}", DialogConfig("SubCards", null, appCard.subListView, DialogButton(DialogButtonType.POSITIVE, "+") {
            dynamicDialog.applyConfig("input")
        }))

        dynamicDialog.addConfig("black_list_${appCard.name}", DialogConfig("BlackList", null, appCard.blackListView, DialogButton(DialogButtonType.POSITIVE, "+") {
            dynamicDialog.applyConfig("input")
        }))

        holder.deleteButton.setOnClickListener {
            delete(holder.cardView, position)
        }

        holder.deleteBackground.setOnClickListener {
            delete(holder.cardView, position)
        }

        holder.subCardsButton.setOnClickListener {
            selectedDialog = "sub_cards_${appCard.name}"
            appCard.dynamicDialog.applyConfig("sub_cards_${appCard.name}")
        }

        holder.blacklistButton.setOnClickListener {
            selectedDialog = "black_list_${appCard.name}"
            appCard.dynamicDialog.applyConfig("black_list_${appCard.name}")
        }

        appCard.cardView = holder.cardView
    }

    // TODO: Whenever a card is deleted, the cards below it move up to fill up its space. Animate this process.

    private fun showKeyboard() {
        inputManager.toggleSoftInput(SHOW_FORCED, 0)
    }

    private fun hideKeyboard() {
        inputManager.toggleSoftInput(0, 0)
    }

    fun deleteAll() {

//        for ((i, appCard) in apps.withIndex()) {
//            delete(appCard.cardView ?: return, i)
//        }

        animateDeletion(*apps.map { card -> card.cardView ?: return }.toTypedArray())
//        apps.forEach { appCard ->
//            animateDeletion(appCard.cardView ?: return)
////            Thread.sleep(150)
//        }
//        apps.clear()
//        notifyDataSetChanged()
    }

    private fun delete(cardView: CardView, position: Int) {
        animateDeletion(cardView)
        cardView.postDelayed({
            apps.removeAt(position)
            notifyDataSetChanged()
        }, 600)
    }

//    private fun animateDeletion(cardViews: List<CardView>) = animateDeletion(*cardViews.toTypedArray())

    private fun animateDeletion(vararg cardViews: CardView) {
        for (cardView in cardViews) {
            ObjectAnimator.ofFloat(cardView.findViewById(R.id.delete_button), "translationX", -1440.0f).apply {
                duration = 1000
            }.start()
            ObjectAnimator.ofFloat(cardView.findViewById(R.id.card_view_layout), "translationX", -1680.0f).apply {
                duration = 1000
            }.start()
            ObjectAnimator.ofFloat(cardView.findViewById(R.id.delete_background), "scaleX", 12.96f).apply {
                duration = 1000
            }.start()
            cardView.postDelayed({
                ObjectAnimator.ofFloat(cardView, "translationX", -1440.0f).apply {
                    duration = 250
                }.start()
            }, 300)
//            cardView.postDelayed({
//                ObjectAnimator.ofFloat(cardView, "scaleX", 0.0f).apply {
//                    duration = 250
//                }.start()
//            }, 300)
//            cardView.postDelayed({
//                ObjectAnimator.ofFloat(cardView, "scaleX", 1.0f).apply {
//                    duration = 250
//                }.start()
//            }, 300)
        }
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