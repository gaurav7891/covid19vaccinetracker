package com.nativework.covid19vaccinetracker.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.nativework.covid19vaccinetracker.base.BaseFragment

class GenerateOTPFragment : BaseFragment() {

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
    ): View? {
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

}