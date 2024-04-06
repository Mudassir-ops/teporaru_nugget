package com.aioapp.nuggetmvp.ui.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.aioapp.nuggetmvp.R
import com.aioapp.nuggetmvp.databinding.FragmentPaymentBinding
import com.aioapp.nuggetmvp.di.datastore.SharedPreferenceUtil
import com.aioapp.nuggetmvp.viewmodels.CartSharedViewModel


class PaymentFragment : Fragment() {

    private var binding: FragmentPaymentBinding? = null
    private val cartSharedViewModel: CartSharedViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentPaymentBinding.inflate(layoutInflater, container, false)
        return binding?.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.headerLayout?.tvCartCount?.text = SharedPreferenceUtil.savedCartItemsCount
        binding?.headerLayout?.tvCartCount?.visibility = View.GONE
        binding?.headerLayout?.ivCart?.visibility = View.GONE
        setPrices()
        binding?.clCard?.setOnTouchListener { _, _ ->
            if (findNavController().currentDestination?.id == R.id.paymentFragment) {
                findNavController().navigate(R.id.action_paymentFragment_to_paymentStatusFragment)
            }
            true
        }
    }

    private fun setPrices() {
        val totalPrice = calculatePrice()
        if (totalPrice != 0.0) {
            binding?.tvTotalPrice?.text = "$".plus(String.format("%.2f", totalPrice))
            val taxAmount = 0.1 * totalPrice
            binding?.tvTaxAmount?.text =
                getString(R.string.tax).plus(String.format("%.2f", taxAmount))
        }
    }

    private fun calculatePrice(): Double {
        var amount = 0.0
        if (!cartSharedViewModel.itemList.value.isNullOrEmpty()) {
            cartSharedViewModel.itemList.value?.let { list ->
                for (item in list) {
                    amount += if (item.count != 0) {
                        (item.price?.toInt() ?: 0) * (item.count)
                    } else {
                        item.price?.toInt() ?: 0
                    }
                }
            }
        }
        return amount
    }

}