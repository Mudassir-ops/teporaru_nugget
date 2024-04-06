package com.aioapp.nuggetmvp.ui.fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.aioapp.nuggetmvp.R
import com.aioapp.nuggetmvp.adapters.DesertViewPagerAdapter
import com.aioapp.nuggetmvp.databinding.FragmentDesertCarouselBinding
import com.aioapp.nuggetmvp.di.datastore.SharedPreferenceUtil
import com.aioapp.nuggetmvp.utils.appextension.colorizeTwoWordsInSentence
import com.aioapp.nuggetmvp.utils.enum.IntentTypes
import com.aioapp.nuggetmvp.viewmodels.CartSharedViewModel
import com.aioapp.nuggetmvp.viewmodels.NuggetProcessingStatus
import com.aioapp.nuggetmvp.viewmodels.NuggetSharedViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.util.Timer
import java.util.TimerTask


class DesertCarouselFragment : Fragment() {
    private var currentPage = 0
    private var timer: Timer? = null
    private var binding: FragmentDesertCarouselBinding? = null
    private val cartSharedViewModel: CartSharedViewModel by activityViewModels()
    private val images = intArrayOf(R.drawable.fudge_brownie, R.drawable.cheese_cake)
    private var adapter: DesertViewPagerAdapter? = null
    private val nuggetSharedViewModel: NuggetSharedViewModel by activityViewModels()
    private var isFirstTime = true


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter = DesertViewPagerAdapter(context ?: return, images)
    }

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
        binding?.headerLayout?.tvCartCount?.text = SharedPreferenceUtil.savedCartItemsCount
        binding?.viewPager?.adapter = adapter
        binding?.tvBottomPrompt?.text =
            getString(R.string.try_i_want_a_fudge_brownie).colorizeTwoWordsInSentence(
                "Fudge Brownie",
                "Pay"
            )
        startAutoSwitching()
        observeState()
    }

    private fun startAutoSwitching() {
        val delay: Long = 5000
        val period: Long = 5000
        val handler = Handler(Looper.getMainLooper())
        val update = Runnable {
            if (currentPage == images.size) {
                currentPage = 0
            }
            if (currentPage == 0) binding?.tvBottomPrompt?.text =
                getString(R.string.try_i_want_a_fudge_brownie).colorizeTwoWordsInSentence(
                    "Fudge Brownie",
                    "Pay"
                ) else if (currentPage == 1) binding?.tvBottomPrompt?.text =
                getString(R.string.try_i_want_a_cheese_cake).colorizeTwoWordsInSentence(
                    "Cheesecake",
                    "Pay"
                )
            binding?.viewPager?.currentItem = currentPage++
        }
        timer = Timer()
        timer?.schedule(object : TimerTask() {
            override fun run() {
                handler.post(update)
            }
        }, delay, period)
    }

    private fun observeState() {
        nuggetSharedViewModel.mState.flowWithLifecycle(
            lifecycle, Lifecycle.State.STARTED
        ).onEach { states ->
            when (states) {
                NuggetProcessingStatus.Init -> Log.e("NuggetMvp", "onViewCreated: Init")

                is NuggetProcessingStatus.RecordingStarted -> handleRecordingStartedState()

                is NuggetProcessingStatus.RecordingEnded -> Log.e(
                    "NuggetMvp", "onViewCreated: Init${states.isEnded}"
                )

                is NuggetProcessingStatus.TranscriptStarted -> binding?.tvBottomPrompt?.text =
                    getString(R.string.transcripitng)

                is NuggetProcessingStatus.ParitialTranscriptionState -> {
                    binding?.tvBottomPrompt?.text = states.value
                }

                is NuggetProcessingStatus.TextToResponseEnded -> {
                    handleTextToResponseEndedState(states)
                }
            }
        }.launchIn(lifecycleScope)
    }

    private fun handleRecordingStartedState() {
        binding?.tvBottomPrompt?.text = getString(R.string.listening)
        binding?.tvBottomPrompt?.setTextColor(
            ContextCompat.getColor(
                context ?: return, R.color.white
            )
        )
    }

    private fun handleTextToResponseEndedState(states: NuggetProcessingStatus.TextToResponseEnded) {
        if (isFirstTime) {
            isFirstTime = false
            return
        }
        when (states.value?.intent) {
            IntentTypes.PAYMENT.label, IntentTypes.DENY.label -> {
                if (findNavController().currentDestination?.id == R.id.desertCarouselFragment) {
                    findNavController().navigate(R.id.action_desertCarouselFragment_to_paymentFragment)
                }
            }

            IntentTypes.ADD.label -> {
                binding?.tvBottomPrompt?.text = getString(R.string.hope_you_will_enjoy_our_desert)
                binding?.tvBottomPrompt?.setTextColor(
                    ContextCompat.getColor(
                        context ?: return,
                        R.color.orange
                    )
                )
                Handler(Looper.getMainLooper()).postDelayed({
                    if (findNavController().currentDestination?.id == R.id.desertCarouselFragment) {
                        findNavController().navigate(R.id.action_desertCarouselFragment_to_paymentFragment)
                    }
                }, 2000)
            }

            else -> {
                if (findNavController().currentDestination?.id == R.id.desertCarouselFragment) {
                    findNavController().navigate(R.id.action_desertCarouselFragment_to_paymentFragment)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        timer?.cancel()
        timer = null
    }


}