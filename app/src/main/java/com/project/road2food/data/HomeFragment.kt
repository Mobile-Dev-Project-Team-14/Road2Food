package com.project.road2food.data

import android.os.Bundle
import android.system.Os.remove
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.project.road2food.MainActivity
import com.project.road2food.R
import kotlinx.android.synthetic.main.activity_main.*

class HomeFragment : Fragment(R.layout.fragment_home) { override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
): View? {

    val view = inflater.inflate(R.layout.fragment_home, container, false)

    val btnmap = view.findViewById<Button>(R.id.btnmap)
    val btnoffers = view.findViewById<Button>(R.id.btnoffers)
    val btnlogin = view.findViewById<Button>(R.id.btnlogin)

    val button = view.findViewById<Button>(R.id.dropDown)

    view.findViewById<Button>(R.id.btnmap)
    btnmap.setOnClickListener {

        val mapFragment = Map1Fragment()
        val transaction: FragmentTransaction = requireFragmentManager().beginTransaction()
        transaction.replace(R.id.fragment_container, mapFragment)
        transaction.commit()
    }

    view.findViewById<Button>(R.id.btnoffers)
    btnoffers.setOnClickListener {
        val offersFragment = OffersFragment()
        val transaction: FragmentTransaction = requireFragmentManager().beginTransaction()
        transaction.replace(R.id.fragment_container, offersFragment)
        transaction.commit()
    }

    view.findViewById<Button>(R.id.btnlogin)
    btnlogin.setOnClickListener {
        val offersFragment = AccountFragment()
        val transaction: FragmentTransaction = requireFragmentManager().beginTransaction()
        transaction.replace(R.id.fragment_container, offersFragment)
        transaction.commit()
    }

    button.setOnClickListener {
        val popupMenu: PopupMenu = PopupMenu(context, button)
        popupMenu.menuInflater.inflate(R.menu.food_menu, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.itdinner -> {
                    val lunchmenu = Lunch_Menu()
                    val transaction: FragmentTransaction =
                        requireFragmentManager().beginTransaction()
                    transaction.replace(R.id.fragment_contain, lunchmenu)
                    transaction.commit()
                }
                R.id.itlunch -> {
                    val lunchmenu = Lunch_Menu()
                    val transaction: FragmentTransaction =
                        requireFragmentManager().beginTransaction()
                    transaction.replace(R.id.fragment_contain, lunchmenu)
                    transaction.commit()
                }
                R.id.itbreakfast -> {
                    val lunchmenu = Lunch_Menu()
                    val transaction: FragmentTransaction =
                        requireFragmentManager().beginTransaction()
                    transaction.replace(R.id.fragment_contain, lunchmenu)
                    transaction.commit()
                }
            }
            true
        })
        popupMenu.show()
    }
    return view
}
}
