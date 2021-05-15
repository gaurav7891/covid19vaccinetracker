package com.nativework.covid19vaccinetracker.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.work.Constraints
import androidx.work.NetworkType
import com.nativework.covid19vaccinetracker.R
import com.nativework.covid19vaccinetracker.base.BaseApp
import com.nativework.covid19vaccinetracker.databinding.ActivityHomeBinding
import com.nativework.covid19vaccinetracker.ui.appointment.CalenderByPincodeFragment


class HomeActivity : BaseApp() {

    private lateinit var binding: ActivityHomeBinding

    private val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setToolbarTitle("Search Vaccine Centers")
        loadFragment(CalenderByDistrictFragment(), R.id.home_container)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.menu_searchByPin -> {
                loadFragment(CalenderByPincodeFragment.newInstance(), R.id.home_container)
            }

            R.id.menu_searchByDistrict -> {
                loadFragment(CalenderByDistrictFragment(), R.id.home_container)
            }
        }
        return super.onOptionsItemSelected(item)
    }
}