package com.nativework.covid19vaccinetracker

import android.os.Bundle
import com.nativework.covid19vaccinetracker.base.BaseApp
import com.nativework.covid19vaccinetracker.ui.GenerateOTPFragment

class MainActivity : BaseApp() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        loadFragment(GenerateOTPFragment.newInstance(), R.id.main_container)
    }
}