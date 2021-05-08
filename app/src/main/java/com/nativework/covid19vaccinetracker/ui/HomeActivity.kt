package com.nativework.covid19vaccinetracker.ui

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.nativework.covid19vaccinetracker.R
import com.nativework.covid19vaccinetracker.base.BaseApp
import com.nativework.covid19vaccinetracker.databinding.ActivityHomeBinding
import com.nativework.covid19vaccinetracker.models.locality.District
import com.nativework.covid19vaccinetracker.models.locality.StatesList
import com.nativework.covid19vaccinetracker.ui.appointment.AppointmentFragment
import com.nativework.covid19vaccinetracker.ui.home.AutocompleteAdapter
import com.nativework.covid19vaccinetracker.utils.AppUtils
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class HomeActivity : BaseApp() {

    private lateinit var binding: ActivityHomeBinding
    private var viewModel: HomeViewModel? = null
    private var adapter: AutocompleteAdapter? = null
    private var stateStringList = ArrayList<String>()
    private var statesList: StatesList? = null
    private var districtList = ArrayList<District>()
    private var districtStringList = ArrayList<String>()
    private var districtId: String? = null
    private var dateSelected: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        setListeners()
        initData()
        setObservers()
    }

    private fun setListeners() {
        binding.autoTextState.setOnItemClickListener { adapterView, _, position, _ ->
            Toast.makeText(
                this,
                adapterView.getItemAtPosition(position) as String,
                Toast.LENGTH_SHORT
            ).show()
            val itemSelected = adapterView.getItemAtPosition(position)
            var stateId = ""
            for (state in statesList!!) {
                if (itemSelected.equals(state.stateName)) {
                    stateId = state.stateId.toString()
                }
            }
            Log.d("tag", stateId)
            if (stateId.isNotEmpty()) {
                viewModel?.getDistrictList(stateId)
            }
        }

        binding.autoTextDistrict.setOnItemClickListener { adapterView, _, position, _ ->
            Toast.makeText(
                this,
                adapterView.getItemAtPosition(position) as String,
                Toast.LENGTH_SHORT
            ).show()

            val itemSelected = adapterView.getItemAtPosition(position)
            for (d in districtList) {
                if (itemSelected.equals(d.district_name)) {
                    districtId = d.district_id.toString()
                }
            }
            districtId?.let { Log.d("tag", it) }
        }

        binding.btnSearch.setOnClickListener {
            val state = binding.autoTextState.text.toString()
            val district = binding.autoTextDistrict.text.toString()
            if (dateSelected.isNullOrEmpty()) {
                dateSelected = getSelectedDate()
            }
            getCentersAvailable(state, district, dateSelected)
        }

        binding.calendarView.setOnDateChangeListener { calendarView, year, month, dayOfMonth ->
            val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.ROOT)
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            dateSelected = sdf.format(calendar.time)
        }
    }

    private fun getCentersAvailable(state: String, district: String, dateSelected: String?) {
        Toast.makeText(
            this,
            dateSelected,
            Toast.LENGTH_SHORT
        ).show()
        if (dateSelected != null) {
            districtId?.let { viewModel?.getCalendarByDistrict(it, dateSelected) }
        }
    }

    private fun getSelectedDate(): String? {
        val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.ROOT)
        return sdf.format(Date(binding.calendarView.date))
    }

    private fun initData() {
        stateStringList = getStateList()
        adapter = AutocompleteAdapter(this, R.layout.item_layout_textview, stateStringList)
        binding.autoTextState.setAdapter(adapter)
        // disable past dates
        val calendar = Calendar.getInstance()
        calendar.apply {
            set(Calendar.HOUR_OF_DAY, Calendar.getInstance().getActualMinimum(Calendar.HOUR_OF_DAY))
        }
        val date = calendar.time.time
        binding.calendarView.minDate = date
    }

    private fun getStateList(): ArrayList<String> {
        statesList = AppUtils.getStatesModelFromJson(R.raw.states, this)
        val list = ArrayList<String>()
        for (state in statesList!!) {
            state.stateName?.let { list.add(it) }
        }
        return list
    }


    private fun setObservers() {

        viewModel?.showProgress?.observe(this, {
            if (it) {
                binding.progressLayout.visibility = View.VISIBLE
            } else {
                binding.progressLayout.visibility = View.GONE
            }
        })

        viewModel?.errorMessage?.observe(this, {
            Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
        })

        viewModel?.getDistrictListData()?.observe(this, {
            if (it.size > 0) {
                districtList = it
                val list = getDistrictNameList(districtList)
                adapter = AutocompleteAdapter(this, R.layout.item_layout_textview, list)
                binding.autoTextDistrict.setAdapter(adapter)
            }
        })
    }

    private fun getDistrictNameList(districtList: java.util.ArrayList<District>?): ArrayList<String> {
        val list = ArrayList<String>()
        if (districtList != null) {
            for (district in districtList) {
                district.district_name?.let { list.add(it) }
            }
        }
        districtStringList = list
        return list
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId){
            R.id.menu_searchByPin -> {
                binding.homeContainer.visibility = View.VISIBLE
                binding.btnSearch.visibility = View.GONE
                loadFragment(AppointmentFragment.newInstance(),R.id.home_container)
            }
        }
        return super.onOptionsItemSelected(item)
    }
}