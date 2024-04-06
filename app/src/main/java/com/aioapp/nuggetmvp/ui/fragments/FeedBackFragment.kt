package com.aioapp.nuggetmvp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.aioapp.nuggetmvp.R
import com.aioapp.nuggetmvp.databinding.FragmentFeedBackBinding
import com.aioapp.nuggetmvp.di.datastore.SharedPreferenceUtil
import com.aioapp.nuggetmvp.viewmodels.CartSharedViewModel

class FeedBackFragment : Fragment() {

    private var binding :FragmentFeedBackBinding?=null
    private val cartSharedViewModel: CartSharedViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentFeedBackBinding.inflate(layoutInflater,container,false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
       binding?.headerLayout?.tvCartCount?.text = SharedPreferenceUtil.savedCartItemsCount
        binding?.headerLayout?.tvCartCount?.visibility = View.GONE
        binding?.headerLayout?.ivCart?.visibility = View.GONE
    }
}