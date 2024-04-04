package com.aioapp.nuggetmvp.ui.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.aioapp.nuggetmvp.R
import com.aioapp.nuggetmvp.databinding.FragmentQuestionForwardBinding
import com.aioapp.nuggetmvp.models.Food
import com.aioapp.nuggetmvp.service.NuggetCameraService


class QuestionForwardFragment : Fragment() {

    private var binding: FragmentQuestionForwardBinding? = null
    private var requiredItem: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentQuestionForwardBinding.inflate(layoutInflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (arguments != null) {
            requiredItem = this.arguments?.getString("RequiredItem") as String
            binding?.tvBottomPrompt?.text =
                getString(R.string.sure_your).plus(" ").plus(requiredItem).plus(" will be here shortly")
        }
        ContextCompat.startForegroundService(
            context ?: return, Intent(context ?: return, NuggetCameraService::class.java)
        )

    }

}