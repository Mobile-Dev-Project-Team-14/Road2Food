package com.project.road2food

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.PopupMenu
import android.widget.TextView
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
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.mapview.*
import org.osmdroid.config.Configuration.getInstance
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.offers.*
import kotlinx.android.synthetic.main.qr_code.*
import kotlinx.android.synthetic.main.user_login.*
import kotlinx.android.synthetic.main.user_registeration.*
import org.osmdroid.views.overlay.Marker
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.ktx.auth
import kotlinx.android.synthetic.main.account.*
import kotlinx.android.synthetic.main.fragment_home.btnmap
import kotlinx.android.synthetic.main.fragment_home.btnoffers
import kotlinx.android.synthetic.main.offer_item.*
import kotlinx.android.synthetic.main.offer_item_user.*
import java.util.*
import kotlin.collections.ArrayList

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
        var activeList = ArrayList<OfferItemUser>()
        val restaurants: MutableList<String> = ArrayList()

        auth = FirebaseAuth.getInstance()
        val uid = auth.currentUser!!.uid


        // ---> Fetch possible offers
        Firebase.firestore.collection("restaurants").get().addOnSuccessListener {
            for (document in it) {
                val restaurantName = document.get("name").toString()
                restaurants.add(document.id)
                Firebase.firestore.collection("restaurants").document(document.id)
                    .collection("offers").document("offer_1").get().addOnSuccessListener {
                        val offer_desc = it.get("description").toString()
                        val start = it.get("start")
                        val target = it.get("target")

                        val item = OfferItem(restaurantName, offer_desc, start, target)
                        list+=item
                    }
                Firebase.firestore.collection("restaurants").document(document.id)
                    .collection("offers").document("offer_2").get().addOnSuccessListener {
                        val offer_desc = it.get("description").toString()
                        val start = it.get("counter")
                        val target = it.get("isActive")

                        val item = OfferItem(restaurantName, offer_desc, start, target)
                        list+=item
                    }
            }
        } // <--- End of fetch offers


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

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
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
            return
        }// <--- End of permission request

        // ---> User location
        fusedLocationClient.lastLocation.addOnSuccessListener {
                location : Location ->
            longitude = location.longitude
            latitude = location.latitude
            initMap()
        }
        fusedLocationClient.lastLocation.addOnFailureListener { println("Location not found") }
        locationPermissionRequest.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        ) // <--- End of user location

        fun setCurrentFragment(fragment: Fragment) =
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.fragment_container, fragment)
                commit()
            }
        fun showQr(){
            val text = "QR code here"
            val encoder = BarcodeEncoder()
            val bitMap = encoder.encodeBitmap(text, BarcodeFormat.QR_CODE, 300, 300)
            ivQRCode.setImageBitmap(bitMap)
            registration_layout.visibility= View.GONE
            login_layout.visibility=View.GONE
            qr_code_layout.visibility=View.VISIBLE
            offers_page.visibility= View.GONE
        }
        fun userOffers() {
            val fetchedList = ArrayList<OfferItemUser>()
            // ---> Fetch current user offers
            if (uid != null) {
                Firebase.firestore.collection("users").document(uid)
                    .collection("user_offers").get().addOnSuccessListener {
                        var offerCount = it.size() - 1
                        offerCount.toString()
                        var counter = offerCount
                        println(offerCount)
                        while (offerCount > 0) {
                            Firebase.firestore.collection("users").document(uid)
                                .collection("user_offers").document("offer_" + offerCount).get()
                                .addOnSuccessListener {
                                    val restaurant_name = it.get("restaurant_name").toString()
                                    val offer_desc = it.get("offer_description").toString()
                                    val start = it.get("offer_start")
                                    val target = it.get("offer_target")
                                    val item = OfferItemUser(restaurant_name, offer_desc, start, target)
                                    fetchedList += item
                                }
                            activeList = fetchedList

                            offerCount--
                        }
                    }
                offerItemsUser(activeList)
            }
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
            userOffers()

        }

        fun showAccount(){
            val currentUser = Firebase.auth.currentUser
            if (currentUser != null ) {
                currentUser?.let {
                    val email = currentUser.email
                    println("*******************************************************")
                    println(email)
                    findViewById<TextView>(R.id.tvAccountEmail).setText(email.toString()).toString()
                }

                offers_layout.visibility = View.GONE
                account_layout.visibility = View.VISIBLE
                home_layout.visibility = View.GONE
                mapview_layout.visibility = View.GONE
                offers_page.visibility = View.GONE
                login_layout.visibility = View.GONE
            } else {
                R.id.nav_account
            }
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
            val currentUser = auth.currentUser
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
                val init = hashMapOf(
                    "email" to email
                )

                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            val userId = auth.currentUser!!.uid
                            Toast.makeText(this, "account created sucessfully!!", Toast.LENGTH_SHORT).show()
                            Firebase.firestore.collection("users").document(userId).collection("user_offers").document("account_details")
                                .set(init)
                            showAccount()
                        } else {
                            Log.w("createUserWithEmail:failure", task.exception)
                            Toast.makeText(baseContext, "Registration failed.",
                                Toast.LENGTH_SHORT).show()
                        }
                    }
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
                                //showAccount()
                            }, 1500)
                        } else {
                            Toast.makeText(this, "wrong id or password!!", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
            }
        }

        logoutButton.setOnClickListener {
            println("Logging out")
            FirebaseAuth.getInstance().signOut();
            showLogIn()
        }

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
            userOffers()
            btnOffers.setBackgroundResource(R.drawable.right_background)
            btnOffers.setTextColor(getColor(R.color.brightRed))
            btnActive.setTextColor(getColor(R.color.white))
            btnActive.setBackgroundResource(R.drawable.left_background)
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
        userOffers()
    } // <--- End of onCreate

    private fun generateItems(offerList: ArrayList<OfferItem>): List<OfferItem>{
        val list = ArrayList<OfferItem>()
        println(offerList)
        for (i in offerList){
            val item = OfferItem(i.name, i.desc, i.start, i.target)
            list += item
        }
        return list
    }
    private fun generateItemsUser(offerList: ArrayList<OfferItemUser>): List<OfferItemUser>{
        val list = ArrayList<OfferItemUser>()
        println(offerList)
        for (i in offerList){
            val item = OfferItemUser(i.name, i.desc, i.start, i.target)
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
    private fun offerItemsUser(list: ArrayList<OfferItemUser>) {
        val restaurants: MutableList<String> = ArrayList()
        Firebase.firestore.collection("restaurants").get().addOnSuccessListener {
            for (document in it){
                restaurants.add(document.id)
            }
            val items = generateItemsUser(list)
            recycler_view.adapter = ItemAdapterUser(items)
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

    private fun showQr(view: View){
        val text = "QR code here"
        val encoder = BarcodeEncoder()
        val bitMap = encoder.encodeBitmap(text, BarcodeFormat.QR_CODE, 300, 300)
        ivQRCode.setImageBitmap(bitMap)
        registration_layout.visibility= View.GONE
        login_layout.visibility=View.GONE
        qr_code_layout.visibility=View.VISIBLE
        offers_page.visibility= View.GONE
    }

    // ---> Initialize map
    private fun initMap() {
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
    // <--- End of map
}
