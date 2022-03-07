package com.edvora.mobiletest.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.edvora.mobiletest.R
import com.edvora.mobiletest.databinding.FragmentRideBinding
import com.edvora.mobiletest.ui.adapter.RiderAdapter
import com.edvora.mobiletest.ui.viewmodel.MainViewModel
import com.google.android.material.tabs.TabLayout

class RideFragment : Fragment() {
    companion object{
        const val KEY_NEWEST = "newest"
        const val KEY_UPCOMING = "upcoming"
        const val KEY_PAST = "past"
        const val KEY_TAB = "com.edvora.mobiletest.KEY_TAB"

        fun getInstance(tab: String): RideFragment{
            return RideFragment().apply {
                arguments = Bundle(1).apply{ putString(KEY_TAB, tab) }
            }
        }
    }

    private val viewModel: MainViewModel by activityViewModels()

    private lateinit var binding: FragmentRideBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return FragmentRideBinding.inflate(inflater).run {
            binding = this

            root
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = RiderAdapter()
        binding.recyclerView.adapter = adapter

        viewModel.rideObserver.observe(this){
            adapter.submitList(it)
            updateTabText(it.size)
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.currentTag = when(arguments!!.getString(KEY_TAB)) {
            KEY_NEWEST -> MainViewModel.TAG_NEAREST
            KEY_UPCOMING -> MainViewModel.TAG_UPCOMING
            else -> MainViewModel.TAG_PAST
        }

        viewModel.getRides()

    }

    private fun updateTabText(size: Int){
        val tabKey = arguments!!.getString(KEY_TAB)
        if(tabKey == KEY_NEWEST) return

        val tabLayout = (requireActivity().findViewById<TabLayout>(R.id.tab_layout))
        val tab = tabLayout.getTabAt(if(tabKey == KEY_UPCOMING) 0 else 1)

        tab!!.text = getString(if(tabKey == KEY_UPCOMING) R.string.upcoming else R.string.past, size)

    }
}