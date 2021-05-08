package com.nativework.covid19vaccinetracker.ui.appointment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.ViewModelProvider
import com.nativework.covid19vaccinetracker.base.BaseFragment
import com.nativework.covid19vaccinetracker.databinding.FragmentAppointmentBinding
import com.nativework.covid19vaccinetracker.ui.center.CenterActivity
import com.nativework.covid19vaccinetracker.utils.Constants
import java.text.SimpleDateFormat
import java.util.*

class AppointmentFragment : BaseFragment() {

    private var _binding: FragmentAppointmentBinding? = null
    private val binding get() = _binding!!
    private var viewModel: AppointmentViewModel? = null
    private var dateSelected: String? = null
    private var pincode:Int? = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this).get(AppointmentViewModel::class.java)
        _binding = FragmentAppointmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object{
        fun newInstance(): AppointmentFragment{
            val args = Bundle()
            val fragment = AppointmentFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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

        binding.btnSearch.setOnClickListener {
            pincode = getPincode()
        }

        binding.btnSearch.setOnClickListener {
            if (dateSelected.isNullOrEmpty() || pincode == 0) {
                dateSelected = getSelectedDate()
                pincode = getPincode()
            }else{
                pincode = getPincode()
                dateSelected?.let { it1 -> pincode?.let { it2 -> getAppointment(it2, it1) } }
            }

        }
    }

    private fun getAppointment(i: Int, date: String) {
        println("-----  $i  and  $date")
        viewModel?.getAppointmentByPincodeAndDate(i, date)
    }

    private fun initData(){
        val calendar = Calendar.getInstance()
        calendar.apply {
            set(Calendar.HOUR_OF_DAY, Calendar.getInstance().getActualMinimum(Calendar.HOUR_OF_DAY))
        }
        val date = calendar.time.time
        binding.calendarView.minDate = date
    }

    private fun  getPincode(): Int {
        val pin :String = binding.edtPincode.text.trim().toString()
        if (!pin.isNullOrEmpty()){
            return pin.toInt()
        }else{
            Toast.makeText(context, "Enter pin code", Toast.LENGTH_SHORT).show()
        }
        return 0
    }

    private fun getSelectedDate(): String? {
        val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.ROOT)
        return sdf.format(Date(binding.calendarView.date))
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
            Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
        })

        viewModel?.getCenterListData()?.observe(this, {
            if (it.size > 0) {
                val intent = Intent(context, CenterActivity::class.java)
                intent.putParcelableArrayListExtra(Constants.INTENT_EXTRA_DATA, it)
                startActivity(intent)
            } else if (it.size == 0) {
                Toast.makeText(context, "Empty/No Schedule found", Toast.LENGTH_LONG).show()
            }
        })
    }
}