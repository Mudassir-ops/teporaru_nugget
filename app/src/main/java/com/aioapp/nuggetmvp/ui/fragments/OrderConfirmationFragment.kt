package com.aioapp.nuggetmvp.ui.fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.aioapp.nuggetmvp.R
import com.aioapp.nuggetmvp.databinding.FragmentOrderConfirmationBinding


class OrderConfirmationFragment : Fragment() {
    private var binding: FragmentOrderConfirmationBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOrderConfirmationBinding.inflate(layoutInflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.orderPreparationAnimView?.playAnimation()
        Handler(Looper.getMainLooper()).postDelayed({
            if (findNavController().currentDestination?.id == R.id.orderConfirmationFragment) {
                findNavController().navigate(R.id.action_orderConfirmationFragment_to_foodOnTheWayFragment)
            }
        }, 2000)

    }
}