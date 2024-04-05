package com.aioapp.nuggetmvp.ui.fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.aioapp.nuggetmvp.R
import com.aioapp.nuggetmvp.databinding.FragmentPaymentStatusBinding
import com.aioapp.nuggetmvp.viewmodels.CartSharedViewModel


class PaymentStatusFragment : Fragment() {

    private var binding: FragmentPaymentStatusBinding? = null
    private val cartSharedViewModel: CartSharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPaymentStatusBinding.inflate(layoutInflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.headerLayout?.tvCartCount?.text =
            cartSharedViewModel.itemList.value?.size.toString()
        Handler(Looper.getMainLooper()).postDelayed({
            binding?.tvStatusOfOrder?.text = getString(R.string.payment_successful)
            binding?.paymentSuccessfulAnim?.visibility = View.VISIBLE
            binding?.paymentSuccessfulAnim?.playAnimation()
            Handler(Looper.getMainLooper()).postDelayed({
                if (findNavController().currentDestination?.id == R.id.paymentStatusFragment) {
                    findNavController().navigate(R.id.action_paymentStatusFragment_to_feedBackFragment)
                }
            }, 1000)
        }, 2000)
    }
}