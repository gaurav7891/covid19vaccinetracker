package com.nativework.covid19vaccinetracker.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import com.nativework.covid19vaccinetracker.base.BaseApp
import com.nativework.covid19vaccinetracker.databinding.ActivitySplashBinding

class SplashActivity : BaseApp() {
    lateinit var binding:ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        Handler().postDelayed({
            val intent = Intent(this, HomeActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
        }, 3000)

    }
}