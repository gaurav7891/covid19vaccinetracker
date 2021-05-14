package com.nativework.covid19vaccinetracker.ui.center

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.nativework.covid19vaccinetracker.base.BaseApp
import com.nativework.covid19vaccinetracker.databinding.ActivityCenterBinding
import com.nativework.covid19vaccinetracker.models.Center
import com.nativework.covid19vaccinetracker.utils.Constants

class CenterActivity : BaseApp() {

    private lateinit var binding: ActivityCenterBinding
    private lateinit var viewModel: CenterViewModel
    private var adapter: CenterAdapter? = null

    private var centerList = ArrayList<Center>()
    private var freeList = ArrayList<Center>()
    private var paidList = ArrayList<Center>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCenterBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        viewModel = ViewModelProvider(this).get(CenterViewModel::class.java)
        title = "Vaccine Centers"
        setListeners()
        initData()
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
            adapter = CenterAdapter(this, centerList)

        }

        if (type == "Free") {
            adapter = CenterAdapter(this, getFreeVaccineCenters(centerList))
        }

        if (type == "Paid") {
            adapter = CenterAdapter(this, getPaidVaccineCenters(centerList))
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
    }

    private fun initData() {
        val intent = intent
        centerList =
            intent.getParcelableArrayListExtra<Center>(Constants.INTENT_EXTRA_DATA) as ArrayList<Center>
        binding.radioAll.isChecked = true
        adapter = CenterAdapter(this, centerList)
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
}