package com.project.road2food

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.View.inflate
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import androidx.activity.compose.setContent
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder
import kotlinx.android.synthetic.main.offer_item.view.*
import kotlinx.android.synthetic.main.offer_item_user.view.itemDesc
import kotlinx.android.synthetic.main.offer_item_user.view.itemName
import kotlinx.android.synthetic.main.offer_item_user.view.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.offer_item_user.*
import kotlinx.android.synthetic.main.qr_code.*
import kotlinx.coroutines.NonDisposableHandle.parent

class ItemAdapterUser(private val itemList: List<OfferItemUser>) : RecyclerView.Adapter<ItemAdapterUser.ItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.offer_item_user, parent, false)
        return ItemViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int){
        val currentItem = itemList[position]
        val db = Firebase.firestore

        holder.restaurantName.text = currentItem.name
        holder.offerDesc.text = currentItem.desc
    }

    override fun getItemCount() = itemList.size

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val restaurantName: TextView = itemView.itemName
        val offerDesc : TextView = itemView.itemDesc
        val btn : Button = itemView.btnTakeOfferUser
    }
}