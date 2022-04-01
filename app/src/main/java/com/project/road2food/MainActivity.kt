package com.project.road2food

//import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.user_login.*
import kotlinx.android.synthetic.main.user_login.registration
import kotlinx.android.synthetic.main.user_registeration.*
import kotlinx.android.synthetic.main.account.*
import com.google.android.material.bottomnavigation.BottomNavigationView
//import com.google.android.material.navigation.NavigationBarView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNavigationView = supportFragmentManager

        registration.setOnClickListener{
            showRegistration()
        }

        log.setOnClickListener{
            showLogIn()
        }

        login.setOnClickListener{
            showAccount()
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
}


