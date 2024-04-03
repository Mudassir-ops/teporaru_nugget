package com.aioapp.nuggetmvp.ui.fragments

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.aioapp.nuggetmvp.R
import com.aioapp.nuggetmvp.databinding.FragmentItemFullViewBinding
import com.aioapp.nuggetmvp.models.Food


class ItemFullViewFragment : Fragment() {

    private var binding: FragmentItemFullViewBinding? = null
    private var foodItem: Food? = null
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
            foodItem = this.arguments?.get("FoodItem") as Food
        }

        binding?.tvItemName?.text = foodItem?.name
        binding?.ivItem?.setImageDrawable(foodItem?.let {
            ContextCompat.getDrawable(
                requireActivity(),
                it.fullImg
            )
        })
        Handler().postDelayed({
            findNavController().navigate(R.id.action_itemFullViewFragment_to_cartFragment)
        }, 10000)
    }
}