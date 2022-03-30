package com.example.bottomnav

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView

class MainActivity : AppCompatActivity() {


    /**
     *
     */
    val mOnNavigationItemSelectedListener: NavigationBarView.OnItemReselectedListener =
        BottomNavigationView.OnNavigationItemReselectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    println("Home")
                    true
                }
                R.id.nav_account -> {
                    println("Account")
                    true
                }
                R.id.nav_map -> {
                    println("Map")
                    true
                }
                R.id.nav_offers -> {
                    println("Offers")
                    true
                }
            }
            false
        }

    /**
     *
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNavigationView = supportFragmentManager

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
    }
}
