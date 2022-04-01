package com.example.bottomnav

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val firstFragment = HomeFragment()
        val secondFragment = AccountFragment()
        val thirdFragment = MapFragment()
        val offersFragment = OffersFragment()

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.home_nav -> setCurrentFragment(firstFragment)
                R.id.account_nav -> setCurrentFragment(secondFragment)
                R.id.map_nav -> setCurrentFragment(thirdFragment)
                R.id.offers_nav -> setCurrentFragment(offersFragment)
            }
            true
        }

    }

    private fun setCurrentFragment(fragment: Fragment) =
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.flfragment, fragment)
            commit()
        }
}




