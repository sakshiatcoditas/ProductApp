package com.example.electronics

import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment?
        
        if (navHostFragment != null) {
            navController = navHostFragment.navController

            findViewById<LinearLayout>(R.id.nav_home).setOnClickListener {
                navController.navigate(R.id.homeFragment)
            }

            findViewById<LinearLayout>(R.id.nav_fav).setOnClickListener {
                navController.navigate(R.id.favFragment)
            }

            findViewById<LinearLayout>(R.id.nav_profile).setOnClickListener {
                navController.navigate(R.id.profileFragment)
            }
        }
    }
}
