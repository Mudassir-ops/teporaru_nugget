package com.aioapp.nuggetmvp.ui.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.aioapp.nuggetmvp.adapters.CartAdapter
import com.aioapp.nuggetmvp.databinding.FragmentCartBinding
import com.aioapp.nuggetmvp.viewmodels.CartSharedViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class CartFragment : Fragment() {

    private var binding: FragmentCartBinding? = null
    private val cartSharedViewModel: CartSharedViewModel by activityViewModels()
    private var cartAdapter: CartAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cartAdapter = CartAdapter(context = context ?: return, cartItemList = listOf())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCartBinding.inflate(layoutInflater, container, false)
        return binding?.root
    }

    @SuppressLint("SuspiciousIndentation")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeCartItems()
        setUpAdapter()
    }

    private fun observeCartItems() {
        cartSharedViewModel.itemList.flowWithLifecycle(
            lifecycle, Lifecycle.State.STARTED
        ).onEach { cartItemList ->
            binding?.headerLayout?.tvCartCount?.text = cartItemList.size.toString()
            cartAdapter?.updateCartItem(cartItemList = cartItemList)
        }.launchIn(lifecycleScope)
    }

    private fun setUpAdapter() {
        binding?.rvCartItems?.run {
            adapter = cartAdapter
            hasFixedSize()
        }
    }
}