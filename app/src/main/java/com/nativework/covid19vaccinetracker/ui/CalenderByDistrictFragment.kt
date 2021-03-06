package com.nativework.covid19vaccinetracker.ui

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.work.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.nativework.covid19vaccinetracker.R
import com.nativework.covid19vaccinetracker.base.BaseApp
import com.nativework.covid19vaccinetracker.base.BaseFragment
import com.nativework.covid19vaccinetracker.databinding.FragmentDistrictCalenderBinding
import com.nativework.covid19vaccinetracker.models.SavedPreferences
import com.nativework.covid19vaccinetracker.models.locality.District
import com.nativework.covid19vaccinetracker.models.locality.StatesList
import com.nativework.covid19vaccinetracker.service.AppointmentNotification
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

class CalenderByDistrictFragment : BaseFragment(), RecentSearchAdapter.OnClickListener {

    private lateinit var binding: FragmentDistrictCalenderBinding
    private var viewModel: HomeViewModel? = null
    private var adapter: AutocompleteAdapter? = null
    private var stateStringList = ArrayList<String>()
    private var statesList: StatesList? = null
    private var districtList = ArrayList<District>()
    private var districtStringList = ArrayList<String>()
    private var districtId: String? = null
    private var dateSelected: String? = null
    private var pinCode: String? = null
    private var stateId: String? = null
    private var recentAdapter: RecentSearchAdapter? = null
    private var isLowerGroup: Boolean = false
    private var isUpperGroup: Boolean = false

    private val constraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .build()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)
        binding = FragmentDistrictCalenderBinding.inflate(layoutInflater)
        val view = binding.root
        viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as BaseApp).setToolbarTitle("Search Vaccine Centers")
        (activity as BaseApp).setToolbarBackButton(false)
        showDistrictSearchOption()
        setListeners()
        initData()
        setObservers()
    }

    private fun setListeners() {
        binding.autoTextState.setOnItemClickListener { adapterView, _, position, _ ->
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
            val itemSelected = adapterView.getItemAtPosition(position)
            for (d in districtList) {
                if (itemSelected.equals(d.district_name)) {
                    districtId = d.district_id.toString()
                }
            }
            districtId?.let { Timber.d("tag %s", it) }
        }

        binding.checkBoxLowerGroup.setOnCheckedChangeListener { _, isChecked ->
            isLowerGroup = isChecked
        }

        binding.checkBoxUpperGroup.setOnCheckedChangeListener { _, isChecked ->
            isUpperGroup = isChecked
        }

        binding.btnSearch.setOnClickListener {
            if (binding.autoTextDistrict.visibility == View.VISIBLE && binding.autoTextState.visibility == View.VISIBLE) {
                val state = binding.autoTextState.text.toString()
                val district = binding.autoTextDistrict.text.toString()
                if (!isPreferenceSelected()) {
                    Toast.makeText(
                        activity,
                        "Please select the notification preference",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }
                if (state.isEmpty()) {
                    Toast.makeText(
                        activity,
                        "Select your state from dropdown for search",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    return@setOnClickListener
                }
                if (district.isEmpty()) {
                    Toast.makeText(
                        activity,
                        "Invalid district in the state selected",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }
                if (dateSelected.isNullOrEmpty()) {
                    dateSelected = getSelectedDate()
                }
                AppUtils.saveSelectedSearch(
                    requireContext(),
                    Constants.SEARCH_BY_DISTRICT,
                    state,
                    district,
                    dateSelected,
                    districtId,
                    stateId,
                    null
                )
                Timber.d(
                    "Notification preference selected from checkbox is Lower = %s, Upper = %s",
                    isLowerGroup,
                    isUpperGroup
                )
                getCentersAvailable(state, district, dateSelected)
            }

            if (binding.edtPincode.visibility == View.VISIBLE) {
                if (dateSelected.isNullOrEmpty()) {
                    dateSelected = getSelectedDate()
                }
                pinCode = binding.edtPincode.text.toString()
                if (pinCode.isNullOrEmpty() || pinCode!!.length > 6) {
                    Toast.makeText(context, "Enter the valid pin code", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                if (!isPreferenceSelected()) {
                    Toast.makeText(
                        activity,
                        "Please select the notification preference",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }

                AppUtils.saveSelectedSearch(
                    requireContext(),
                    Constants.SEARCH_BY_PINCODE,
                    null,
                    null,
                    dateSelected,
                    null,
                    null,
                    pinCode
                )

                pinCode?.let {
                    getCentersByPinCode(dateSelected, it)
                }
            }
        }

        binding.calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.ROOT)
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            dateSelected = sdf.format(calendar.time)
        }
    }

    private fun getCentersByPinCode(dateSelected: String?, pinCode: String) {
        dateSelected?.let { viewModel?.getAppointmentByPinCodeAndDate(pinCode.toInt(), it) }
    }

    private fun isPreferenceSelected(): Boolean {
        return binding.checkBoxLowerGroup.isChecked || binding.checkBoxUpperGroup.isChecked
    }

    private fun getCentersAvailable(state: String, district: String, dateSelected: String?) {
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
        adapter =
            AutocompleteAdapter(requireContext(), R.layout.item_layout_textview, stateStringList)
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

    private fun getStateList(): ArrayList<String> {
        statesList = AppUtils.getStatesModelFromJson(R.raw.states, requireContext())
        val list = ArrayList<String>()
        for (state in statesList!!) {
            state.stateName?.let { list.add(it) }
        }
        return list
    }

    private fun showSavedSearchPref() {
        val jsonString =
            PreferenceConnector.readString(requireContext(), PreferenceConnector.SAVED_PREF, "")
        val type: Type = object : TypeToken<ArrayList<SavedPreferences>>() {}.type
        val model = Gson().fromJson<ArrayList<SavedPreferences>>(jsonString, type)
        if (model != null) {
            binding.textView.visibility = View.VISIBLE
            recentAdapter = RecentSearchAdapter(requireContext(), model, this)
            binding.recentSearchRecyclerView.layoutManager = LinearLayoutManager(requireContext())
            binding.recentSearchRecyclerView.adapter = recentAdapter
        }else{
            binding.textView.visibility = View.GONE
        }
    }

    private fun setObservers() {

        viewModel?.showProgress?.observe(viewLifecycleOwner) {
            if (it) {
                binding.progressLayout.visibility = View.VISIBLE
            } else {
                binding.progressLayout.visibility = View.GONE
            }
        }

        viewModel?.errorMessage?.observe(viewLifecycleOwner) {
            Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
        }

        viewModel?.getDistrictListData()?.observe(viewLifecycleOwner) {
            if (it.size > 0) {
                districtList = it
                val list = getDistrictNameList(districtList)
                adapter = AutocompleteAdapter(requireContext(), R.layout.item_layout_textview, list)
                binding.autoTextDistrict.setAdapter(adapter)
            }
        }

        viewModel?.getCenterListData()?.observe(viewLifecycleOwner) {
            if (it.size > 0) {
                //save the notification preference
                AppUtils.saveNotificationPref(activity, isLowerGroup, isUpperGroup)
                Timber.d("isLower %s isUpperGroup %s", isLowerGroup, isUpperGroup)
                setNotification(districtId, pinCode)
                Timber.d("District ID: %s", districtId)
                Timber.d("Pincode ID: %s", pinCode)
                val intent = Intent(context, CenterActivity::class.java)
                intent.putExtra(Constants.DISTRICT_ID, districtId)
                intent.putExtra(Constants.PINCODE, pinCode)
                intent.putExtra(Constants.STATE_ID, stateId)
                intent.putParcelableArrayListExtra(Constants.INTENT_EXTRA_DATA, it)
                startActivity(intent)
            }else{
                Toast.makeText(activity, "No centers available", Toast.LENGTH_SHORT).show()
            }
        }
        viewModel?.emptyListMessage?.observe(viewLifecycleOwner, {
            Toast.makeText(activity, it, Toast.LENGTH_SHORT).show()
        })
    }

    private fun setNotification(districtId: String?, pinCode: String?) {
        if (districtId != null) {
            activity?.let {
                PreferenceConnector.writeString(
                    it,
                    PreferenceConnector.DISTRICT_ID,
                    districtId
                )
            }
        }
        if (pinCode != null) {
            activity?.let {
                PreferenceConnector.writeString(
                    it,
                    PreferenceConnector.PINCODE,
                    pinCode
                )
            }
        }
        val data = Data.Builder()
        data.putString(Constants.DISTRICT_ID, districtId)
        data.putString(Constants.PINCODE, pinCode)
        val periodWork = PeriodicWorkRequest.Builder(
            AppointmentNotification::class.java,
            Constants.REPEAT_INTERVAL,
            TimeUnit.MINUTES,
            Constants.FLEX_INTERVAL,
            TimeUnit.MINUTES
        )
            .addTag("vaccination-periodic-notification")
            .setConstraints(constraints)
            .setInputData(data.build())
            .build()
        activity?.let {
            WorkManager.getInstance(it).enqueueUniquePeriodicWork(
                "vaccination-periodic-notification",
                ExistingPeriodicWorkPolicy.REPLACE,
                periodWork
            )
        }
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

    override fun onImageClick(savedPref: SavedPreferences?) {
        savedPref?.let {
            if (savedPref.type == Constants.SEARCH_BY_DISTRICT) {
                savedPref.districtId?.let { it1 ->
                    savedPref.date?.let { it2 ->
                        viewModel?.getCalendarByDistrict(
                            it1,
                            it2
                        )
                    }
                }
            }

            if (savedPref.type == Constants.SEARCH_BY_PINCODE) {
                savedPref.pinCode?.toInt()?.let { it1 ->
                    savedPref.date?.let { it2 ->
                        viewModel?.getAppointmentByPinCodeAndDate(
                            it1, it2
                        )
                    }
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        activity?.menuInflater?.inflate(R.menu.main_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.menu_searchByPin -> {
                showPinCodeSearchOption()
                return true
            }
            R.id.menu_searchByDistrict -> {
                showDistrictSearchOption()
                return true
            }

            R.id.stop_notification -> {
                activity?.let {
                    WorkManager.getInstance(it)
                        .cancelAllWorkByTag("vaccination-periodic-notification")
                }
                Toast.makeText(activity, "Notifications Disabled", Toast.LENGTH_SHORT).show()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showDistrictSearchOption() {
        binding.autoTextDistrict.visibility = View.VISIBLE
        binding.autoTextState.visibility = View.VISIBLE
        binding.edtPincode.visibility = View.GONE
        binding.txtMessage.text = getString(R.string.please_search_the_available_centers_for_vaccine_by_district)
    }

    private fun showPinCodeSearchOption() {
        binding.autoTextDistrict.visibility = View.GONE
        binding.autoTextState.visibility = View.GONE
        binding.edtPincode.visibility = View.VISIBLE
        binding.txtMessage.text = getString(R.string.please_search_the_available_centers_for_vaccine_by_pin_code)
    }
}