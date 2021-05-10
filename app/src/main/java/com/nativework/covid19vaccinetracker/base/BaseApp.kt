package com.nativework.covid19vaccinetracker.base

import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

open class BaseApp : AppCompatActivity() {

    private var backPressedToExitOnce = false

    open fun loadFragment(fragment: Fragment, containerId: Int) {
        val fragmentManager = supportFragmentManager
        fragmentManager.beginTransaction()
            .replace(containerId, fragment)
            .commit()
    }

    open fun setToolbarBackButton(isBackButtonEnabled: Boolean) {
        supportActionBar?.setDisplayHomeAsUpEnabled(isBackButtonEnabled)
    }

    open fun setToolbarTitle(toolbarTitle: String) {
        title = toolbarTitle
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
            }
        }
        return super.onOptionsItemSelected(item)
    }

  /*  override fun onBackPressed() {
        if (backPressedToExitOnce) {
            super.onBackPressed()
            return
        }
        backPressedToExitOnce = true
        Toast.makeText(this, "Press back to exit again", Toast.LENGTH_SHORT).show()

        Handler().postDelayed(Runnable { backPressedToExitOnce = false }, 2000)
        *//*Handler(Looper.getMainLooper()).postAtTime({
            backPressedToExitOnce = false
        },2000)*//*
    }*/

    override fun onBackPressed() {
        super.onBackPressed()
        if(supportFragmentManager.backStackEntryCount > 0){
            supportFragmentManager.popBackStack()
        }else{
            super.onBackPressed()
        }
    }
}