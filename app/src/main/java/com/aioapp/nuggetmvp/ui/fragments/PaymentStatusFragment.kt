package com.aioapp.nuggetmvp.ui.fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.aioapp.nuggetmvp.R
import com.aioapp.nuggetmvp.databinding.FragmentPaymentStatusBinding


class PaymentStatusFragment : Fragment() {

    private var binding: FragmentPaymentStatusBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPaymentStatusBinding.inflate(layoutInflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Handler(Looper.getMainLooper()).postDelayed({
            binding?.tvStatusOfOrder?.text = getString(R.string.payment_successful)
            binding?.paymentSuccessfulAnim?.visibility = View.VISIBLE
            binding?.paymentSuccessfulAnim?.playAnimation()
        }, 3000)
    }
}