package com.nativework.covid19vaccinetracker.ui

import android.os.Bundle
import com.nativework.covid19vaccinetracker.R
import com.nativework.covid19vaccinetracker.base.BaseApp
import com.nativework.covid19vaccinetracker.databinding.ActivityHomeBinding


class HomeActivity : BaseApp() {

    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setToolbarTitle("Search Vaccine Centers")
        loadFragment(CalenderByDistrictFragment(), R.id.home_container)
    }
}