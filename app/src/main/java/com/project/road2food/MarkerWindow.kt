package com.project.road2food

import android.widget.Button
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.infowindow.InfoWindow

class MarkerWindow(private val info: MapView):
    InfoWindow(R.layout.restaurant_map_info, info) {

    override fun onOpen(item: Any?) {
        closeAllInfoWindowsOn(mapView)

        val moveButton = mView.findViewById<Button>(R.id.move_button)
        val deleteButton = mView.findViewById<Button>(R.id.delete_button)

        moveButton.setOnClickListener {
        }
        deleteButton.setOnClickListener {
        }


        mView.setOnClickListener {
            close()
        }
    }

    override fun onClose() {
        TODO("Not yet implemented")
    }

}