package com.nativework.covid19vaccinetracker.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.nativework.covid19vaccinetracker.MainActivity
import com.nativework.covid19vaccinetracker.R
import com.nativework.covid19vaccinetracker.base.BaseFragment
import com.nativework.covid19vaccinetracker.databinding.FragmentGenerateOtpBinding
import com.nativework.covid19vaccinetracker.models.GenerateOTPRequest
import com.nativework.covid19vaccinetracker.ui.otp.OTPViewModel

class GenerateOTPFragment : BaseFragment() {

    private var _binding: FragmentGenerateOtpBinding? = null
    private val binding get() = _binding!!
    private var viewModel: OTPViewModel? = null

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(): GenerateOTPFragment {
            val args = Bundle()
            val fragment = GenerateOTPFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this).get(OTPViewModel::class.java)
        _binding = FragmentGenerateOtpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnGenerateOTP.setOnClickListener {
            val phoneNumber = binding.editPhoneNumber.text.toString()
            if (phoneNumber.isEmpty()) {
                Toast.makeText(activity, "Enter valid mobile Number", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val request = GenerateOTPRequest(phoneNumber)
            getOTP(request)
        }
    }

    private fun getOTP(request: GenerateOTPRequest) {
        viewModel?.generateOTP(request)
        viewModel?.txnId?.observe(this, {
            if (!it.isNullOrEmpty()) {
                (activity as MainActivity).loadFragment(
                    ConfirmOTPFragment.newInstance(it),
                    R.id.main_container
                )
            }
        })
    }

}