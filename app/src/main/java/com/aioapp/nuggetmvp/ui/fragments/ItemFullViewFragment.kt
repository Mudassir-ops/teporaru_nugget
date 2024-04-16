package com.aioapp.nuggetmvp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.aioapp.nuggetmvp.R
import com.aioapp.nuggetmvp.databinding.FragmentItemFullViewBinding
import com.aioapp.nuggetmvp.models.Food
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class ItemFullViewFragment : Fragment() {

    private var binding: FragmentItemFullViewBinding? = null
    private var foodItem: Food? = null
    private var desertName: String? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentItemFullViewBinding.inflate(layoutInflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (arguments != null) {
            foodItem =
                if (this.arguments?.get("FoodItem") != null) this.arguments?.get("FoodItem") as Food else null
            desertName =
                if (this.arguments?.getString("DesertName") != null) this.arguments?.getString("DesertName") else null
        }
        if (foodItem != null) {
            binding?.tvItemName?.text = foodItem?.logicalName
            binding?.ivItem?.setImageDrawable(foodItem?.let {
                ContextCompat.getDrawable(
                    requireActivity(),
                    it.fullImg
                )
            })
        } else {
            binding?.tvItemName?.text = desertName
            if (desertName.equals("Cheesecake",true)) {
                binding?.ivItem?.setImageResource(
                    R.drawable.cheese_cake_full_item
                )
            } else {
                binding?.ivItem?.setImageResource(
                    R.drawable.fudge_brownie_full_item
                )
            }

        }

        lifecycleScope.launch {
            delay(5000)
            if (findNavController().currentDestination?.id == R.id.itemFullViewFragment) {
                if (desertName != null)
                    findNavController().navigate(R.id.action_itemFullViewFragment_to_paymentFragment)
                else
                    findNavController().navigate(R.id.action_itemFullViewFragment_to_cartFragment)

            }
        }
    }
}