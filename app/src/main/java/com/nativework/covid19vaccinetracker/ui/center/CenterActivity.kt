package com.nativework.covid19vaccinetracker.ui.center

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.nativework.covid19vaccinetracker.R
import com.nativework.covid19vaccinetracker.base.BaseApp
import com.nativework.covid19vaccinetracker.databinding.ActivityCenterBinding
import com.nativework.covid19vaccinetracker.models.Center
import com.nativework.covid19vaccinetracker.utils.Constants

class CenterActivity : BaseApp() {

    private lateinit var binding: ActivityCenterBinding
    private var adapter:CenterAdapter?=null
    private var centerList = ArrayList<Center>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCenterBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        initData()
    }

    private fun initData() {
        val intent = intent
        centerList = intent.getParcelableArrayListExtra<Center>(Constants.INTENT_EXTRA_DATA) as ArrayList<Center>
        adapter = CenterAdapter(this, centerList)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
    }
}