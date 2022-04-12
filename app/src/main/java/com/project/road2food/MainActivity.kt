package com.project.road2food

//import androidx.appcompat.app.AppCompatActivity
//import com.google.android.material.navigation.NavigationBarView

import android.Manifest
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.text.TextUtils.replace
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import com.google.android.gms.location.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.project.road2food.data.AccountFragment
import com.project.road2food.data.HomeFragment
import com.project.road2food.data.OffersFragment
import com.project.road2food.data.model.Map1Fragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.mapview.*
import kotlinx.android.synthetic.main.user_login.*
import kotlinx.android.synthetic.main.user_login.registration
import kotlinx.android.synthetic.main.user_registeration.*
import org.osmdroid.config.Configuration.*
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.*

class MainActivity : AppCompatActivity() {

  //  val home = HomeFragment()
  //  val account = AccountFragment()
  //  val maps = Map1Fragment()
 //   val offers = OffersFragment()


    private val REQUEST_PERMISSIONS_REQUEST_CODE = 1;
    private lateinit var map : MapView

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    var latitude: Double = 125.000
    var longitude: Double = 111.000

    // ---> Start of onCreate
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this))

        setContentView(R.layout.activity_main)


        map = findViewById<MapView>(R.id.mapView)
        map.setTileSource(TileSourceFactory.MAPNIK)

        // Request permissions to access device location --->
        val locationPermissionRequest = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) {
            permissions ->
            when {
                permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                    // Precise location granted
                }
                permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                    // Approx location granted
                } else -> {
                    // Permission denied
                    println("Permission denied")
                }
            }
        }
            locationPermissionRequest.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        // <--- End of permission request

        // ---> Initialize map
        fun initMap() {
            val mapController = map.controller
            mapController.setZoom(14.5)
            val startPoint = GeoPoint(latitude, longitude)
            println(startPoint)
            mapController.setCenter(startPoint)
            map.setMultiTouchControls(true)

            val userPosition = Marker(mapView)
            var geoPoint = GeoPoint(latitude, longitude)
            userPosition.position = geoPoint

            userPosition.setAnchor(Marker.ANCHOR_BOTTOM, Marker.ANCHOR_CENTER)
            userPosition.title = "You're here!"
            userPosition.icon = ContextCompat.getDrawable(this, R.drawable.ic_you)
            mapView.overlays.add(userPosition)

            val db = Firebase.firestore
            db.collection("restaurants").get().addOnSuccessListener {
                for (restaurant in it){
                    val restaurantMarker = Marker(mapView)
                    val geo = restaurant.getGeoPoint("coordinates")
                    val latitude: Double = geo!!.latitude
                    val longitude: Double = geo!!.longitude
                    val restaurantPos = GeoPoint(latitude, longitude)
                    restaurantMarker.position = restaurantPos
                    restaurantMarker.icon = ContextCompat.getDrawable(this, R.drawable.ic_location_pin)
                    val infoWindow = MarkerWindow(mapView, restaurant.id)
                    restaurantMarker.infoWindow = infoWindow

                    mapView.overlays.add(restaurantMarker)
                    mapView.invalidate()
                }
            }
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationClient.lastLocation.addOnSuccessListener {
            location : Location ->
            longitude = location.longitude
            latitude = location.latitude
            initMap()
        }
        fusedLocationClient.lastLocation.addOnFailureListener { println("Location not found") }
        // <--- End of map
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
    } // <--- End of onCreate

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

// override fun onCreate(savedInstanceState: Bundle?) {
 //   super.onCreate(savedInstanceState)
   // setContentView(R.layout.activity_main)

   // val home = HomeFragment()
 //   val map = Map1Fragment()
  //  val discount = OffersFragment()
  //  val account = AccountFragment()

  //  setCurrentFragment(home)

 //   bottom_navigation.setOnItemSelectedListener {
 //       when (it.itemId) {
   //         R.id.nav_home -> setCurrentFragment(home)
    //        R.id.nav_map -> setCurrentFragment(map)
     //       R.id.nav_offers -> setCurrentFragment(discount)
    //        R.id.nav_account -> setCurrentFragment(account)
   //     }
    //    true
  //  }
// }

//private fun setCurrentFragment(fragment: Fragment) =
  //  supportFragmentManager.beginTransaction().apply {
    //    replace(R.id.fragment_container, fragment)
      //  commit()
   // }
//}
