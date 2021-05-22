package com.nativework.covid19vaccinetracker.ui.appointment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import com.nativework.covid19vaccinetracker.R
import com.nativework.covid19vaccinetracker.base.BaseApp
import com.nativework.covid19vaccinetracker.base.BaseFragment
import com.nativework.covid19vaccinetracker.databinding.FragmentPincodeCalenderBinding
import com.nativework.covid19vaccinetracker.ui.center.CenterActivity
import com.nativework.covid19vaccinetracker.utils.Constants
import java.text.SimpleDateFormat
import java.util.*

class CalenderByPincodeFragment : BaseFragment() {

    private lateinit var binding: FragmentPincodeCalenderBinding
    private var viewModel: AppointmentViewModel? = null
    private var dateSelected: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPincodeCalenderBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get(AppointmentViewModel::class.java)
        setHasOptionsMenu(true)
        return binding.root
    }

    companion object {
        fun newInstance(): CalenderByPincodeFragment {
            val args = Bundle()
            val fragment = CalenderByPincodeFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as BaseApp).setToolbarTitle("Search By Pin Code")
        setListeners()
        initData()
        setObservers()
    }

    private fun setListeners() {
        binding.calendarView.setOnDateChangeListener { calendarView, year, month, dayOfMonth ->
            val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.ROOT)
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            dateSelected = sdf.format(calendar.time)
        }

        binding.btnSearchByPin.setOnClickListener {
            val pincode = binding.edtPincode.text.toString()
            if (pincode.isEmpty()) {
                Toast.makeText(context, "Please enter the pincode to search", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            if (dateSelected.isNullOrEmpty()) {
                dateSelected = getSelectedDate()
            }
            dateSelected?.let { it1 -> getAppointment(pincode.toInt(), it1) }
        }
    }

    private fun getAppointment(i: Int, date: String) {
        viewModel?.getAppointmentByPincodeAndDate(i, date)
    }

    private fun initData() {
        val calendar = Calendar.getInstance()
        calendar.apply {
            set(Calendar.HOUR_OF_DAY, Calendar.getInstance().getActualMinimum(Calendar.HOUR_OF_DAY))
        }
        val date = calendar.time.time
        binding.calendarView.minDate = date
    }

    private fun getSelectedDate(): String? {
        val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.ROOT)
        return sdf.format(Date(binding.calendarView.date))
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

        viewModel?.getCenterListData()?.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                val intent = Intent(context, CenterActivity::class.java)
                intent.putParcelableArrayListExtra(Constants.INTENT_EXTRA_DATA, it)
                startActivity(intent)
            }
        }

        viewModel?.isDataEmpty?.observe(viewLifecycleOwner) {
            val tempCode = binding.edtPincode.text.toString().trim()
            Toast.makeText(
                context,
                "No Schedule available for pincode $tempCode  on $dateSelected",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        val menuPin = menu.findItem(R.id.menu_searchByPin)
        menuPin.isEnabled = false
        return
    }
}