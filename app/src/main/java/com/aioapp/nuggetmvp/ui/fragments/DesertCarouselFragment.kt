package com.aioapp.nuggetmvp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.aioapp.nuggetmvp.R
import com.aioapp.nuggetmvp.adapters.ImageAdapter
import com.aioapp.nuggetmvp.databinding.FragmentDesertCarouselBinding
import com.aioapp.nuggetmvp.viewmodels.CartSharedViewModel


class DesertCarouselFragment : Fragment() {

    private var binding: FragmentDesertCarouselBinding? = null
    private val cartSharedViewModel: CartSharedViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentDesertCarouselBinding.inflate(layoutInflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.headerLayout?.tvCartCount?.text =
            cartSharedViewModel.itemList.value?.size.toString()
        binding?.rvCarousel?.adapter = ImageAdapter(
            context ?: return,
            listOf(R.drawable.fudge_brownie, R.drawable.cheese_cake)
        )
    }

}