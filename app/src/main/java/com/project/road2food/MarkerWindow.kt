package com.project.road2food

import android.widget.Button
import android.widget.TextView
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.toUpperCase
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.infowindow.InfoWindow

class MarkerWindow(private val info: MapView, var id: String):
    InfoWindow(R.layout.restaurant_map_info, info) {

    override fun onOpen(item: Any?) {
        closeAllInfoWindowsOn(mapView)
        println(id)
        Firebase.firestore.collection("restaurants").document(id)
            .get().addOnSuccessListener {
                val name = it.get("name")
                val category = it.get("category")
                val link = it.get("link")
                val hours = it.get("openhours")
                val featured = it.get("featured")
                val address = it.get("address")
                val postalCode = it.get("postalcode")

                mView.findViewById<TextView>(R.id.tvInfoboxTitle).setText(name.toString()).toString()
                mView.findViewById<TextView>(R.id.tvInfoboxCategory).setText("Category: " + category.toString().capitalize()).toString()
                mView.findViewById<TextView>(R.id.tvInfoboxFeatured).setText(featured.toString()).toString()
                mView.findViewById<TextView>(R.id.tvInfoboxHours).setText(hours.toString()).toString()
                mView.findViewById<TextView>(R.id.tvInfoboxLink).setText(link.toString()).toString()
                mView.findViewById<TextView>(R.id.tvInfoboxAddress).setText(address.toString()).toString()
                mView.findViewById<TextView>(R.id.tvInfoboxPostal).setText(postalCode.toString()).toString()
            }

        val closeInfoBox = mView.findViewById<Button>(R.id.closeInfo)
        val viewOffers = mView.findViewById<Button>(R.id.btnViewOffers)

        closeInfoBox.setOnClickListener {
            close()
        }
        viewOffers.setOnClickListener {
        }
    }

    override fun onClose() {
        return
    }

}