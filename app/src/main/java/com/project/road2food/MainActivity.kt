package com.project.road2food

//import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.user_login.*
import kotlinx.android.synthetic.main.user_login.registration
import kotlinx.android.synthetic.main.user_registeration.*
import kotlinx.android.synthetic.main.account.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        registration.setOnClickListener{
            showRegistration()
        }

        log.setOnClickListener{
            showLogIn()
        }

        login.setOnClickListener{
            showAccount()
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


