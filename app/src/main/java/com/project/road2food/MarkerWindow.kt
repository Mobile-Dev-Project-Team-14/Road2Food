package com.project.road2food

import android.widget.Button
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.infowindow.InfoWindow

class MarkerWindow(private val info: MapView):
    InfoWindow(R.layout.restaurant_map_info, info) {

    override fun onOpen(item: Any?) {
        closeAllInfoWindowsOn(mapView)

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