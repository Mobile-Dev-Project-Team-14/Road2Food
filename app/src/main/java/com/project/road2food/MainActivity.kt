package com.project.road2food

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.user_login.*
import kotlinx.android.synthetic.main.user_registeration.*


//import com.google.android.material.navigation.NavigationBarView

class MainActivity : AppCompatActivity() {


    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        auth = FirebaseAuth.getInstance()

/* function for registration button*/
        registration_btn.setOnClickListener {
            val email = login_email.text.toString().trim()
            val password = login_password.text.toString().trim()

            if (email.isNotEmpty() || password.isNotEmpty()) {

                auth.createUserWithEmailAndPassword(email, password)
                Toast.makeText(this, "account created sucessfully!!", Toast.LENGTH_SHORT).show()
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
                        } else {
                            Toast.makeText(this, "wrong id or password!!", Toast.LENGTH_SHORT)
                                .show()
                        }


                    }




                supportFragmentManager

                registration.setOnClickListener {
                    showRegistration()
                }

                log.setOnClickListener {
                    showLogIn()
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


            /*private fun showAccount() {
                registration_layout.visibility = View.GONE
                login_layout.visibility = View.GONE
                account_layout.visibility = View.VISIBLE
            }*/

        }
    }

    private fun showLogIn() {
        registration_layout.visibility = View.GONE
        login_layout.visibility = View.VISIBLE
    }

    private fun showRegistration() {
        registration_layout.visibility = View.VISIBLE
        login_layout.visibility = View.GONE
    }
}


