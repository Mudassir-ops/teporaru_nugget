package com.aioapp.nuggetmvp.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aioapp.nuggetmvp.R
import com.aioapp.nuggetmvp.databinding.FragmentClosingBinding
import com.aioapp.nuggetmvp.ui.SplashActivity


class ClosingFragment : Fragment() {

    private var binding: FragmentClosingBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentClosingBinding.inflate(layoutInflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.closingAnimation?.playAnimation()

        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(activity ?: return@postDelayed, SplashActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            activity?.finish()
            activity?.finishAffinity()
        }, 1000)

    }
}