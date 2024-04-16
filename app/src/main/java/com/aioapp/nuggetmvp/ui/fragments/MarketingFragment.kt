package com.aioapp.nuggetmvp.ui.fragments

import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.aioapp.nuggetmvp.R
import com.aioapp.nuggetmvp.databinding.FragmentMarketingBinding


class MarketingFragment : Fragment() {
    private var packageName = "com.aioapp.nuggetmvp"
    private var binding: FragmentMarketingBinding? = null
    private val videoUrls = listOf(
        "android.resource://$packageName/${R.raw.video2}",
        "android.resource://$packageName/${R.raw.video3}",
        "android.resource://$packageName/${R.raw.video1}",
        "android.resource://$packageName/${R.raw.video2}",
        "android.resource://$packageName/${R.raw.video3}"
    )
    private var currentIndex = 0
    private var videosPlayed = 0
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMarketingBinding.inflate(layoutInflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.videoView?.setVideoURI(Uri.parse("android.resource://$packageName/${R.raw.video1}"))
        binding?.videoView?.setOnPreparedListener { mediaPlayer ->
            // Start video playback when prepared
            mediaPlayer.start()
        }
        playNextVideoWithDelay()
    }

    private fun playNextVideoWithDelay() {
        val delayMillis = 5000L // 5 seconds delay, adjust as needed
        // Delay changing the video after the specified delay
        handler.postDelayed({
            // If all videos have been played, stop playback
            if (videosPlayed >= videoUrls.size) {
                binding?.videoView?.stopPlayback()
                return@postDelayed
            }
            binding?.videoView?.setVideoURI(Uri.parse(videoUrls[currentIndex]))
            binding?.videoView?.setOnPreparedListener { mediaPlayer ->

                mediaPlayer.start()
            }
            binding?.videoView?.setOnCompletionListener { mediaPlayer ->
                mediaPlayer.reset()
                videosPlayed++
                if (videosPlayed < videoUrls.size) {
                    currentIndex = (currentIndex + 1) % videoUrls.size
                    playNextVideoWithDelay()
                } else {
                    binding?.videoView?.stopPlayback()
                    if (findNavController().currentDestination?.id == R.id.marketingFragment) {
                        findNavController().navigate(R.id.action_marketingFragment_to_foodOnTheWayFragment)
                    }
                }
            }
        }, delayMillis)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }

    override fun onStop() {
        super.onStop()
    }

}