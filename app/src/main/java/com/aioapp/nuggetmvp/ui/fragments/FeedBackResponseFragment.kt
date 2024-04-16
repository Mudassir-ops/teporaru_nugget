package com.aioapp.nuggetmvp.ui.fragments

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.aioapp.nuggetmvp.R
import com.aioapp.nuggetmvp.databinding.FragmentFeedBackResponseBinding
import com.aioapp.nuggetmvp.di.datastore.SharedPreferenceUtil
import com.aioapp.nuggetmvp.models.ParametersEntity
import com.aioapp.nuggetmvp.viewmodels.CartSharedViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FeedBackResponseFragment : Fragment() {
    private var binding: FragmentFeedBackResponseBinding? = null
    private val cartSharedViewModel: CartSharedViewModel by activityViewModels()
    private val mediaPlayer = MediaPlayer()
    private var parameters: ParametersEntity? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentFeedBackResponseBinding.inflate(layoutInflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (arguments != null) {
            parameters = this.arguments?.get("Parameters") as ParametersEntity
        }
        binding?.headerLayout?.tvCartCount?.text = SharedPreferenceUtil.savedCartItemsCount
        binding?.headerLayout?.tvCartCount?.visibility = View.GONE
        binding?.headerLayout?.ivCart?.visibility = View.GONE
        reviewSound()
        setViews()
        lifecycleScope.launch {
            withContext(IO) {
                delay(4000)
                withContext(Main) {
                    if (mediaPlayer.isPlaying)
                        mediaPlayer.stop()
                    mediaPlayer.release()
                    if (findNavController().currentDestination?.id == R.id.feedBackResponseFragment) {
                        findNavController().navigate(R.id.action_feedBackResponseFragment_to_closingFragment)
                    }
                }
            }
        }
    }

    private fun setViews() {
        if (parameters != null) {
            when (parameters?.level) {
                "loved it" -> {
                    binding?.tvFeedback?.text = getString(R.string.loved_it_ex)
                    binding?.ivFeedback?.setImageResource(R.drawable.loved_it)
                    binding?.tvFeedbackResponse?.text =
                        getString(R.string.glad_you_enjoyed_this_experience)
                }

                "liked it" -> {
                    binding?.tvFeedback?.text = getString(R.string.liked_it_ex)
                    binding?.ivFeedback?.setImageResource(R.drawable.liked_it)
                    binding?.tvFeedbackResponse?.text = getString(R.string.happy_to_serve)
                }

                "okay" -> {
                    binding?.tvFeedback?.text = getString(R.string.okay)
                    binding?.ivFeedback?.setImageResource(R.drawable.okay)
                    binding?.tvFeedbackResponse?.text = getString(R.string.happy_to_serve)
                }

                "meh" -> {
                    binding?.tvFeedback?.text = getString(R.string.meh)
                    binding?.ivFeedback?.setImageResource(R.drawable.meh)
                    binding?.tvFeedbackResponse?.text =
                        getString(R.string.oh_no_i_m_still_a_work_in_progress_i_ll_learn_to_serve_better_next_time)
                }

                "hated it" -> {
                    binding?.tvFeedback?.text = getString(R.string.hated_it)
                    binding?.ivFeedback?.setImageResource(R.drawable.hated_it)
                    binding?.tvFeedbackResponse?.text =
                        getString(R.string.thanks_for_your_feedback_i_ll_do_better_next_time)

                }
            }

        }
    }

    private fun reviewSound() {
        try {
            val soundFile = resources.openRawResourceFd(R.raw.review_audio)
            mediaPlayer.setDataSource(
                soundFile.fileDescriptor,
                soundFile.startOffset,
                soundFile.length
            )
            mediaPlayer.prepare()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}