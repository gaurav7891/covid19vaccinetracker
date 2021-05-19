package com.nativework.covid19vaccinetracker.ui.center

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.nativework.covid19vaccinetracker.base.BaseApp
import com.nativework.covid19vaccinetracker.databinding.ActivityVaccineCenterBinding
import com.nativework.covid19vaccinetracker.models.Center
import com.nativework.covid19vaccinetracker.models.Session
import com.nativework.covid19vaccinetracker.utils.AppUtils
import com.nativework.covid19vaccinetracker.utils.Constants

class VaccineCenterActivity : BaseApp() {

    private lateinit var binding: ActivityVaccineCenterBinding
    var adapter: SlotAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVaccineCenterBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setToolbarTitle("Vaccine Center")
        initData()
        setListeners()
    }

    private fun setListeners() {
        binding.btnCoWinPortal.setOnClickListener {
            // check if app is installed
            var launchIntent: Intent? = null
            launchIntent = if (AppUtils.isAppInstalled(this, Constants.COWIN_PACKAGE_NAME)) {
                packageManager.getLaunchIntentForPackage(Constants.COWIN_PACKAGE_NAME)
            } else {
                // redirect on CoWIN portal https://www.cowin.gov.in/home
                Intent(Intent.ACTION_VIEW, Uri.parse(Constants.COWIN_PORTAL))
            }
            launchIntent?.let { startActivity(launchIntent) }
        }
    }

    private fun initData() {
        val notificationIntent = intent
        val center = notificationIntent.getParcelableExtra<Center>(Constants.CENTER_DETAILS)
        center?.let { c ->
            binding.txtCenterName.text = c.name
            binding.txtCenterAddress.text = c.address
            binding.txVaccinationType.text = c.fee_type
            binding.txtPinCode.text = c.pincode.toString()
            val sessions = c.sessions
            if (!sessions.isNullOrEmpty()) {
                adapter = SlotAdapter(this, sessions as ArrayList<Session>)
                binding.slotRecyclerView.layoutManager = LinearLayoutManager(this)
                binding.slotRecyclerView.adapter = adapter
            }
        }
    }
}