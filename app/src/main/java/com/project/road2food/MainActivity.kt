package com.project.road2food


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.project.road2food.data.AccountFragment
import com.project.road2food.data.HomeFragment
import com.project.road2food.data.Map1Fragment
import com.project.road2food.data.OffersFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val home = HomeFragment()
        val account = AccountFragment()
        val map = Map1Fragment()
        val discount = OffersFragment()


        setCurrentFragment(home)

        bottom_navigation.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.nav_home -> setCurrentFragment(home)
                R.id.nav_account -> setCurrentFragment(account)
                R.id.nav_map -> setCurrentFragment(map)
                R.id.nav_offers -> setCurrentFragment(discount)
            }
            true
        }
    }

    private fun setCurrentFragment(fragment: Fragment) =
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragment_container, fragment)
            commit()
        }
}
