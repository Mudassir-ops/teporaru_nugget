package com.aioapp.nuggetmvp.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aioapp.nuggetmvp.R
import com.aioapp.nuggetmvp.databinding.FragmentQuestionForwardBinding


class QuestionForwardFragment : Fragment() {

    private var binding :FragmentQuestionForwardBinding ?= null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentQuestionForwardBinding.inflate(layoutInflater,container,false)
        return binding?.root
    }

}