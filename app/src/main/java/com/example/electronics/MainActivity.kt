package com.example.electronics

import android.os.Bundle
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.electronics.R

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize NavController from nav_host_fragment
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment?
        
        if (navHostFragment != null) {
            navController = navHostFragment.navController

        // Set up manual bottom nav clicks
        findViewById<LinearLayout>(R.id.nav_home).setOnClickListener {
                try {
            navController.navigate(R.id.homeFragment)
                } catch (e: Exception) {
                    Toast.makeText(this, "Navigation error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }

        findViewById<LinearLayout>(R.id.nav_fav).setOnClickListener {
                try {
            navController.navigate(R.id.favFragment)
                } catch (e: Exception) {
                    Toast.makeText(this, "Navigation error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }

        findViewById<LinearLayout>(R.id.nav_profile).setOnClickListener {
                try {
            navController.navigate(R.id.profileFragment)
                } catch (e: Exception) {
                    Toast.makeText(this, "Navigation error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(this, "Navigation setup failed", Toast.LENGTH_LONG).show()
        }
    }
}
