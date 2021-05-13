package com.nativework.covid19vaccinetracker.ui.center

import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.nativework.covid19vaccinetracker.base.BaseApp
import com.nativework.covid19vaccinetracker.databinding.ActivityCenterBinding
import com.nativework.covid19vaccinetracker.models.Center
import com.nativework.covid19vaccinetracker.service.AppointmentNotification
import com.nativework.covid19vaccinetracker.utils.Constants
import java.util.concurrent.TimeUnit

class CenterActivity : BaseApp(), CenterAdapter.VaccineCenterClickListener {

    private lateinit var binding: ActivityCenterBinding
    private lateinit var viewModel: CenterViewModel
    private var adapter: CenterAdapter? = null

    private var centerList = ArrayList<Center>()
    private var freeList = ArrayList<Center>()
    private var paidList = ArrayList<Center>()

    // default minimum periodic work time interval is 15 minutes
    private var timeInterval = 15

    private val constraints = Constraints.Builder()
        .setRequiresBatteryNotLow(true)
        .build()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCenterBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        viewModel = ViewModelProvider(this).get(CenterViewModel::class.java)
        title = "Vaccine Centers"
        setListeners()
        initData()
        setObservers()
    }

    private fun setObservers() {
        viewModel.setNotificationAlert().observe(this, {
            val periodWork = PeriodicWorkRequest.Builder(
                AppointmentNotification::class.java,
                it.toLong(),
                TimeUnit.MINUTES,
                5,
                TimeUnit.MINUTES)
                .addTag("vaccination-periodic-notification")
                .setConstraints(constraints)
                .build()
            //PreferenceConnector.writeString(this,PreferenceConnector.LAST_PERIODIC_TIME, System.currentTimeMillis().toString())
            WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                "vaccination-periodic-notification",
                ExistingPeriodicWorkPolicy.KEEP,
                periodWork
            )
            Toast.makeText(this, "Set the periodic work for $it", Toast.LENGTH_SHORT).show()
        })
    }

    private fun setListeners() {
        binding.radioAll.setOnCheckedChangeListener { compoundButton, isChecked ->
            if (isChecked) {
                setRecyclerView("All")
            }
        }

        binding.radioFree.setOnCheckedChangeListener { compoundButton, isChecked ->
            if (isChecked) {
                setRecyclerView("Free")
            }
        }

        binding.radioPaid.setOnCheckedChangeListener { compoundButton, isChecked ->
            if (isChecked) {
                setRecyclerView("Paid")
            }
        }
    }

    private fun setRecyclerView(type: String) {
        if (type == "All") {
            adapter = CenterAdapter(this, centerList, this)

        }

        if (type == "Free") {
            adapter = CenterAdapter(this, getFreeVaccineCenters(centerList), this)
        }

        if (type == "Paid") {
            adapter = CenterAdapter(this, getPaidVaccineCenters(centerList), this)
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
    }

    private fun initData() {
        val intent = intent
        centerList =
            intent.getParcelableArrayListExtra<Center>(Constants.INTENT_EXTRA_DATA) as ArrayList<Center>
        binding.radioAll.isChecked = true
        adapter = CenterAdapter(this, centerList, this)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
    }

    private fun getPaidVaccineCenters(centerList: java.util.ArrayList<Center>): ArrayList<Center> {

        for (cen in centerList) {

            if (cen.fee_type == "Paid") {
                paidList.add(cen)
            }
        }
        return paidList
    }

    private fun getFreeVaccineCenters(centerList: java.util.ArrayList<Center>): ArrayList<Center> {

        for (cen in centerList) {
            if (cen.fee_type == "Free") {
                freeList.add(cen)
            }
        }
        return freeList
    }

    override fun onNotifyVaccineAvailabilityClicked(center: Center) {
        viewModel.setNotificationForAppointment(timeInterval)
    }

    override fun onNotificationCanceled(center: Center) {
        WorkManager.getInstance(this).cancelAllWorkByTag("vaccination-periodic-notification")
        Toast.makeText(this, "Notification cancelled", Toast.LENGTH_SHORT).show()
    }
}