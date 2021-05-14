package com.nativework.covid19vaccinetracker.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.work.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.nativework.covid19vaccinetracker.R
import com.nativework.covid19vaccinetracker.base.BaseApp
import com.nativework.covid19vaccinetracker.databinding.ActivityHomeBinding
import com.nativework.covid19vaccinetracker.models.SavedPreferences
import com.nativework.covid19vaccinetracker.models.locality.District
import com.nativework.covid19vaccinetracker.models.locality.StatesList
import com.nativework.covid19vaccinetracker.service.AppointmentNotification
import com.nativework.covid19vaccinetracker.ui.appointment.AppointmentFragment
import com.nativework.covid19vaccinetracker.ui.center.CenterActivity
import com.nativework.covid19vaccinetracker.ui.home.AutocompleteAdapter
import com.nativework.covid19vaccinetracker.ui.home.RecentSearchAdapter
import com.nativework.covid19vaccinetracker.utils.AppUtils
import com.nativework.covid19vaccinetracker.utils.Constants
import com.nativework.covid19vaccinetracker.utils.PreferenceConnector
import timber.log.Timber
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList


class HomeActivity : BaseApp(), RecentSearchAdapter.OnClickListener {

    private lateinit var binding: ActivityHomeBinding
    private var viewModel: HomeViewModel? = null
    private var adapter: AutocompleteAdapter? = null
    private var stateStringList = ArrayList<String>()
    private var statesList: StatesList? = null
    private var districtList = ArrayList<District>()
    private var districtStringList = ArrayList<String>()
    private var districtId: String? = null
    private var dateSelected: String? = null
    private var stateId: String? = null
    private var recentAdapter: RecentSearchAdapter? = null

    private val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        title = "Search Vaccine Centers"
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
            districtId?.let { Timber.d("tag %s", it) }
        }

        binding.btnSearch.setOnClickListener {
            val state = binding.autoTextState.text.toString()
            val district = binding.autoTextDistrict.text.toString()
            if (dateSelected.isNullOrEmpty()) {
                dateSelected = getSelectedDate()
            }
            AppUtils.saveSelectedSearch(this, state, district, dateSelected, districtId, stateId)
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

        //retrieve saved search pref
        showSavedSearchPref()
        // disable past dates
        val calendar = Calendar.getInstance()
        calendar.apply {
            set(Calendar.HOUR_OF_DAY, Calendar.getInstance().getActualMinimum(Calendar.HOUR_OF_DAY))
        }
        val date = calendar.time.time
        binding.calendarView.minDate = date
    }

    private fun showSavedSearchPref() {
        val jsonString = PreferenceConnector.readString(this, PreferenceConnector.SAVED_PREF, "")
        val type: Type = object : TypeToken<ArrayList<SavedPreferences>>() {}.type
        val model = Gson().fromJson<ArrayList<SavedPreferences>>(jsonString, type)
        if (model != null) {
            recentAdapter = RecentSearchAdapter(this, model, this)
            binding.recentSearchRecyclerView.layoutManager = LinearLayoutManager(this)
            binding.recentSearchRecyclerView.adapter = recentAdapter
        }
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

        viewModel?.getCenterListData()?.observe(this, {
            if (it.size > 0) {
                setNotification(districtId)
                val intent = Intent(this, CenterActivity::class.java)
                intent.putExtra(Constants.DISTRICT_ID, districtId)
                intent.putExtra(Constants.STATE_ID, stateId)
                intent.putParcelableArrayListExtra(Constants.INTENT_EXTRA_DATA, it)
                startActivity(intent)
            }
        })
    }

    private fun setNotification(districtId: String?) {
        if (districtId != null) {
            PreferenceConnector.writeString(this, PreferenceConnector.DISTRICT_ID, districtId)
        }
        val data = Data.Builder()
        data.putString(Constants.DISTRICT_ID, districtId)
        val periodWork = PeriodicWorkRequest.Builder(
                AppointmentNotification::class.java,
                Constants.REPEAT_INTERVAL,
                TimeUnit.MINUTES,
                Constants.FLEX_INTERVAL,
                TimeUnit.MINUTES)
                .addTag("vaccination-periodic-notification")
                .setConstraints(constraints)
                .setInputData(data.build())
                .build()
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                "vaccination-periodic-notification",
                ExistingPeriodicWorkPolicy.KEEP,
                periodWork
        )
        Timber.d("Notification set successfully %s", periodWork)
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

    override fun onImageClick(center: SavedPreferences) {
        center.districtId?.let {
            center.date?.let { it1 ->
                viewModel?.getCalendarByDistrict(
                        it,
                        it1
                )
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_searchByPin -> {
                binding.homeContainer.visibility = View.VISIBLE
                binding.btnSearch.visibility = View.GONE
                loadFragment(AppointmentFragment.newInstance(), R.id.home_container)
            }
        }
        return super.onOptionsItemSelected(item)
    }
}