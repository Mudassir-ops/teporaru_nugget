package com.aioapp.nuggetmvp.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.aioapp.nuggetmvp.databinding.FragmentOrderConfirmationBinding
import com.aioapp.nuggetmvp.service.NuggetCameraService


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

        ContextCompat.startForegroundService(
            context ?: return, Intent(context ?: return, NuggetCameraService::class.java)
        )
    }
}