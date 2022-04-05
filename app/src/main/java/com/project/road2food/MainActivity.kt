package com.project.road2food

//import androidx.appcompat.app.AppCompatActivity
//import com.google.android.material.navigation.NavigationBarView

import android.location.Location
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.preference.PreferenceManager
import com.google.android.gms.location.LocationRequest
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.account.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.user_login.*
import kotlinx.android.synthetic.main.user_login.registration
import kotlinx.android.synthetic.main.user_registeration.*
import org.osmdroid.config.Configuration.*
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

class MainActivity : AppCompatActivity() {
    private val REQUEST_PERMISSIONS_REQUEST_CODE = 1;
    private lateinit var map : MapView




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this))

        setContentView(R.layout.activity_main)

        map = findViewById<MapView>(R.id.map)
        map.setTileSource(TileSourceFactory.MAPNIK);
        val mapController = map.controller
        mapController.setZoom(12.5)
        val startPoint = GeoPoint(65.01236, 25.46816);
        mapController.setCenter(startPoint);



        val bottomNavigationView = supportFragmentManager

        registration.setOnClickListener{
            showRegistration()
        }

        log.setOnClickListener{
            showLogIn()
        }

        login.setOnClickListener{
            showMap()
            //showAccount()
        }

        val navView: BottomNavigationView = findViewById(R.id.bottom_navigation)
        navView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    Toast.makeText(this, "Home selected", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.nav_account -> {
                    Toast.makeText(this, "Photos selected", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.nav_map -> {
                    Toast.makeText(this, "More selected", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.nav_offers -> {
                    Toast.makeText(this, "More selected", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> true
            }
        }
    } // end of onCreate

    override fun onResume() {
        super.onResume()
        map.onResume()
    }

    override fun onPause() {
        super.onPause()
        map.onPause()
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        val permissionsToRequest = ArrayList<String>();
        var i = 0;
        while (i < grantResults.size) {
            permissionsToRequest.add(permissions[i]);
            i++;
        }
        if (permissionsToRequest.size > 0) {
            ActivityCompat.requestPermissions(
                this,
                permissionsToRequest.toTypedArray(),
                REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

private fun showRegistration(){
    registration_layout.visibility= View.VISIBLE
    login_layout.visibility=View.GONE
}
    private fun showLogIn(){
        registration_layout.visibility= View.GONE
        login_layout.visibility=View.VISIBLE
    }
    private fun showAccount(){
        registration_layout.visibility= View.GONE
        login_layout.visibility=View.GONE
        account_layout.visibility=View.VISIBLE
    }
    private fun showMap(){
        registration_layout.visibility= View.GONE
        login_layout.visibility=View.GONE
        account_layout.visibility=View.GONE
        mapview_layout.visibility=View.VISIBLE
    }
}


