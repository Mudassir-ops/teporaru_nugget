package com.aioapp.nuggetmvp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.aioapp.nuggetmvp.R
import com.aioapp.nuggetmvp.databinding.FragmentOrderConfirmationBinding
import com.aioapp.nuggetmvp.utils.Constants


class OrderConfirmationFragment : Fragment() {
    private var binding: FragmentOrderConfirmationBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentOrderConfirmationBinding.inflate(layoutInflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (Constants.cartItemList?.size!! > 0)
            binding?.headerLayout?.tvCartCount?.text = Constants.cartItemList?.size.toString()
    }
}