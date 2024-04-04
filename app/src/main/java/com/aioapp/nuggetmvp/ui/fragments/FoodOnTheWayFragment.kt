package com.aioapp.nuggetmvp.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.aioapp.nuggetmvp.R
import com.aioapp.nuggetmvp.databinding.FragmentFoodOnTheWayBinding
import com.aioapp.nuggetmvp.service.NuggetCameraService


class FoodOnTheWayFragment : Fragment() {

    private var binding: FragmentFoodOnTheWayBinding? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentFoodOnTheWayBinding.inflate(layoutInflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.orderIsOnTheWayAnim?.playAnimation()
        Handler().postDelayed({
            if (findNavController().currentDestination?.id == R.id.foodOnTheWayFragment) {
                findNavController().navigate(R.id.action_foodOnTheWayFragment_to_questionsFragment)
            }
        }, 2000)

    }

}