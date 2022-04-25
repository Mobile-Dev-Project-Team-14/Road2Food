package com.project.road2food

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.offer_item.view.*
import kotlinx.coroutines.NonDisposableHandle.parent


class ItemAdapter(private val itemList: List<OfferItem>) : RecyclerView.Adapter<ItemAdapter.ItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.offer_item, parent, false)
        return ItemViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val currentItem = itemList[position]
        val db = Firebase.firestore

        holder.restaurantName.text = currentItem.name
        holder.offerDesc.text = currentItem.desc
        holder.btn.setOnClickListener {

            val auth = FirebaseAuth.getInstance()
            val currentUser = auth.currentUser
            val addOffer = hashMapOf(
                "restaurant_name" to currentItem.name,
                "offer_description" to currentItem.desc,
                "offer_start" to currentItem.start,
                "offer_target" to currentItem.target
            )

            db.collection("users").document(currentUser!!.uid).collection("user_offers")
                .get().addOnSuccessListener {
                    var totalOffers = it.size().toString()
                    if (it.size() < 1) {
                        db.collection("users").document(currentUser!!.uid).collection("user_offers")
                            .document("offer_1").set(addOffer)
                    } else {
                        db.collection("users").document(currentUser!!.uid).collection("user_offers")
                            .document("offer_" + totalOffers).set(addOffer)
                    }
                }
        }
    }

    override fun getItemCount() = itemList.size

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val restaurantName: TextView = itemView.itemName
        val offerDesc : TextView = itemView.itemDesc
        val btn : Button = itemView.btnTakeOffer
    }
}