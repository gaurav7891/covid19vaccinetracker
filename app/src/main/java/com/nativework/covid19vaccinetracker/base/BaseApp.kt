package com.nativework.covid19vaccinetracker.base

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

open class BaseApp : AppCompatActivity() {

    open fun loadFragment(fragment: Fragment, containerId: Int) {
        val fragmentManager = supportFragmentManager
        fragmentManager.beginTransaction()
            .replace(containerId, fragment)
            .commit()
    }
}