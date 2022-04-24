package com.project.road2food

import android.Manifest
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.preference.PreferenceManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.project.road2food.data.AccountFragment
import com.project.road2food.data.HomeFragment
import com.project.road2food.data.OffersFragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.mapview.*
import org.osmdroid.config.Configuration.getInstance
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.*
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder
import com.project.road2food.data.Lunch_Menu
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.offers.*
import kotlinx.android.synthetic.main.qr_code.*
import kotlinx.android.synthetic.main.user_login.*
import kotlinx.android.synthetic.main.user_registeration.*
import org.osmdroid.views.overlay.Marker
import android.view.LayoutInflater
import android.widget.RelativeLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.android.synthetic.main.account.*
import kotlinx.android.synthetic.main.fragment_home.btnmap
import kotlinx.android.synthetic.main.fragment_home.btnoffers
import kotlinx.android.synthetic.main.fragment_home.view.*
import kotlinx.android.synthetic.main.lunchmenu.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.schedule

class MainActivity : AppCompatActivity() {
    private val REQUEST_PERMISSIONS_REQUEST_CODE = 1;
    private lateinit var map : MapView

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    var latitude: Double = 125.000
    var longitude: Double = 111.000

    private lateinit var auth: FirebaseAuth

    // ---> Start of onCreate
    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_main)

        val list = ArrayList<OfferItem>()
        val activeList = ArrayList<OfferItem>()
        val restaurants: MutableList<String> = ArrayList()

        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser

        Firebase.firestore.collection("restaurants").get().addOnSuccessListener {
            for (document in it) {
                val restaurantName = document.get("name").toString()
                restaurants.add(document.id)
                Firebase.firestore.collection("restaurants").document(document.id)
                    .collection("offers").document("offer_1").get().addOnSuccessListener {
                        val offer_desc = it.get("description").toString()
                        val item = OfferItem(restaurantName, offer_desc)
                        list+=item
                    }
                Firebase.firestore.collection("restaurants").document(document.id)
                    .collection("offers").document("offer_2").get().addOnSuccessListener {
                        val offer_desc = it.get("description").toString()
                        val item = OfferItem(restaurantName, offer_desc)
                        list+=item
                    }
            }
        }

        super.onCreate(savedInstanceState)
        getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this))

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

        fun setCurrentFragment(fragment: Fragment) =
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.fragment_container, fragment)
                commit()
            }

        fun showHome(){
            offers_layout.visibility= View.GONE
            account_layout.visibility=View.GONE
            home_layout.visibility=View.VISIBLE
            mapview_layout.visibility=View.GONE
            offers_page.visibility= View.GONE
        }
        fun showOffers(){
            offers_layout.visibility= View.VISIBLE
            account_layout.visibility=View.GONE
            home_layout.visibility=View.GONE
            mapview_layout.visibility=View.GONE
            offers_page.visibility= View.GONE
        }
        fun showAccount(){
            offers_layout.visibility= View.GONE
            account_layout.visibility=View.VISIBLE
            home_layout.visibility=View.GONE
            mapview_layout.visibility=View.GONE
            offers_page.visibility= View.GONE
            login_layout.visibility=View.GONE
        }
        fun showMap(){
            offers_layout.visibility= View.GONE
            account_layout.visibility=View.GONE
            home_layout.visibility=View.GONE
            mapview_layout.visibility=View.VISIBLE
            offers_page.visibility= View.GONE
        }
        fun showOffersPage(){
            offers_page.visibility= View.VISIBLE
            account_layout.visibility=View.GONE
            home_layout.visibility=View.GONE
            mapview_layout.visibility=View.GONE
            offers_layout.visibility= View.GONE
        }
        fun showLogIn() {
            if (currentUser != null) {
                showAccount()
            } else {
                offers_page.visibility = View.GONE
                registration_layout.visibility = View.GONE
                offers_layout.visibility = View.GONE
                account_layout.visibility = View.GONE
                home_layout.visibility = View.GONE
                mapview_layout.visibility = View.GONE
                login_layout.visibility = View.VISIBLE
            }
        }

        fun showRegistration() {
            registration_layout.visibility = View.VISIBLE
            login_layout.visibility = View.GONE
            offers_layout.visibility= View.GONE
            account_layout.visibility=View.GONE
            home_layout.visibility=View.GONE
            mapview_layout.visibility=View.GONE
            //else -> true
        }

        val bottomNavigationView = supportFragmentManager

        registration.setOnClickListener{
            showRegistration()
        }

/* function for registration button*/
        registration_btn.setOnClickListener {
            val email = login_email.text.toString().trim()
            val password = login_password.text.toString().trim()

            if (email.isNotEmpty() || password.isNotEmpty()) {
                val userOffers = hashMapOf(
                    "name" to "offer_1"
                )

                auth.createUserWithEmailAndPassword(email, password)
                Toast.makeText(this, "account created sucessfully!!", Toast.LENGTH_SHORT).show()
                Firebase.firestore.collection("users").document(email).set(userOffers)
                println(email)

                //Firebase.firestore.collection("users").add()
            } else {
                Toast.makeText(this, "fill every field", Toast.LENGTH_SHORT).show()
            }
        }
/*login button function to login*/
        login_btn.setOnClickListener {
            val email = user_email.text.toString().trim()
            val password = user_password.text.toString().trim()

            if (email.isNotEmpty() || password.isNotEmpty()) {
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Login sucessful", Toast.LENGTH_SHORT).show()
                            Handler().postDelayed({
                                showAccount()
                            }, 1500)
                        } else {
                            Toast.makeText(this, "wrong id or password!!", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
            }
        }
/*
        logoutButton.setOnClickListener {
            FirebaseAuth.getInstance().signOut();
            showLogIn()
        }
*/
        bottom_navigation.setOnItemSelectedListener {
                   when (it.itemId) {
                       R.id.nav_home -> showHome()
                       R.id.nav_map -> showMap()
                       R.id.nav_offers ->  showOffersPage() 
                       R.id.nav_account -> showLogIn()
                 }
                true
              }
                log.setOnClickListener {
                    showLogIn()
              }

        btnOffers.setOnClickListener {
            btnOffers.setBackgroundResource(R.drawable.right_background_red)
            btnOffers.setTextColor(getColor(R.color.white))
            btnActive.setTextColor(getColor(R.color.brightRed))
            btnActive.setBackgroundResource(R.drawable.left_background_white)

            offerItems(list)
        }
        btnActive.setOnClickListener {
            btnOffers.setBackgroundResource(R.drawable.right_background)
            btnOffers.setTextColor(getColor(R.color.brightRed))
            btnActive.setTextColor(getColor(R.color.white))
            btnActive.setBackgroundResource(R.drawable.left_background)

            offerItems(activeList)
        }
        btnmap.setOnClickListener {
            bottom_navigation.selectedItemId = R.id.nav_map
        }
        btnoffers.setOnClickListener {
            showOffers()
        }

        dropDown.setOnClickListener {

            //val PopupMenu = findViewById<Button>(R.id.dropDown)
            val popupMenu: PopupMenu = PopupMenu(this, dropDown)
            popupMenu.menuInflater.inflate(R.menu.food_menu, popupMenu.menu)
            popupMenu.show()
            popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.itdinner -> {

                    }
                    R.id.itlunch -> {

                    }
                    R.id.itbreakfast -> {

                    }
                }
                true
            })
        }

    } // <--- End of onCreate

    private fun generateItems(offerList: ArrayList<OfferItem>): List<OfferItem>{
        val list = ArrayList<OfferItem>()
        println(offerList)
        for (i in offerList){
            val item = OfferItem(i.name, i.desc)
            list += item
        }
        return list
    }

    private fun offerItems(list: ArrayList<OfferItem>) {
        val restaurants: MutableList<String> = ArrayList()
        Firebase.firestore.collection("restaurants").get().addOnSuccessListener {
            for (document in it){
                restaurants.add(document.id)
            }
            val items = generateItems(list)
            recycler_view.adapter = ItemAdapter(items)
            recycler_view.layoutManager = LinearLayoutManager(this)
            recycler_view.setHasFixedSize(true)
        }
    }

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

    private fun showQr(){
        val text = "QR code here"
        val encoder = BarcodeEncoder()
        val bitMap = encoder.encodeBitmap(text, BarcodeFormat.QR_CODE, 300, 300)
        ivQRCode.setImageBitmap(bitMap)

        registration_layout.visibility= View.GONE
        login_layout.visibility=View.GONE
        qr_code_layout.visibility=View.VISIBLE
        offers_page.visibility= View.GONE
    }
}
