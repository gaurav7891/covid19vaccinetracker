package com.nativework.covid19vaccinetracker.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.nativework.covid19vaccinetracker.base.BaseFragment
import com.nativework.covid19vaccinetracker.databinding.FragmentConfirmOtpBinding
import com.nativework.covid19vaccinetracker.models.ConfirmOTPRequest
import com.nativework.covid19vaccinetracker.ui.otp.OTPViewModel
import com.nativework.covid19vaccinetracker.utils.AppUtils
import com.nativework.covid19vaccinetracker.utils.Constants

class ConfirmOTPFragment : BaseFragment() {

    private var _binding: FragmentConfirmOtpBinding? = null
    private val binding get() = _binding!!
    private var viewModel: OTPViewModel? = null

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(txnId: String): ConfirmOTPFragment {
            val args = Bundle()
            args.putString(Constants.TXN_ID, txnId)
            val fragment = ConfirmOTPFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnConfirmOTP.setOnClickListener {
            val otp = binding.editOTPNumber.text.toString()
            if (otp.isEmpty()) {
                Toast.makeText(activity, "Invalid OTP", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val txnId = arguments?.getString(Constants.TXN_ID)
            val encodedOTP = AppUtils.getSha256Hash(otp)
            if (encodedOTP!=null){
                val request = txnId?.let { it1 -> ConfirmOTPRequest(encodedOTP, it1) }
                confirmOTP(request)
            }
        }
    }

    private fun confirmOTP(request: ConfirmOTPRequest?) {
        if (request != null) {
            viewModel?.confirmOTP(request)
        }
        viewModel?.token?.observe(this, Observer {
            if (it.isNotEmpty()) {
                Toast.makeText(activity, "Verified", Toast.LENGTH_SHORT).show()
                val intent = Intent(activity, HomeActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                intent.putExtra(Constants.TOKEN, it)
                startActivity(intent)
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this).get(OTPViewModel::class.java)
        _binding = FragmentConfirmOtpBinding.inflate(inflater, container, false)
        return binding.root
    }
}