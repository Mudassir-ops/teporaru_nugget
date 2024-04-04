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
import com.aioapp.nuggetmvp.databinding.FragmentFoodOnTheWayBinding


class FoodOnTheWayFragment : Fragment() {

    private var binding: FragmentFoodOnTheWayBinding? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFoodOnTheWayBinding.inflate(layoutInflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.orderIsOnTheWayAnim?.playAnimation()
        Handler(Looper.getMainLooper()).postDelayed({
            if (findNavController().currentDestination?.id == R.id.foodOnTheWayFragment) {
                findNavController().navigate(R.id.action_foodOnTheWayFragment_to_questionsFragment)
            }
        }, 2000)

    }

}