package com.example.electronics.ui.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.example.electronics.MainActivity
import com.example.electronics.R
import com.example.electronics.databinding.ActivitySplashBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)


        supportActionBar?.hide()


        val fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in)

        // Apply animation to logo and text
        binding.ivLogo.startAnimation(fadeInAnimation)
        binding.tvAppName.startAnimation(fadeInAnimation)
        binding.tvTagline.startAnimation(fadeInAnimation)

        // Navigate to MainActivity after 2.5 seconds (giving time for animation)
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish() // Close splash activity so user can't go back
        }, 2500)
    }
}