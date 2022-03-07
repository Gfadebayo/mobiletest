package com.edvora.mobiletest

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Spannable
import android.text.style.TextAppearanceSpan
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.viewModels
import androidx.core.text.toSpannable
import androidx.core.widget.TextViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.bumptech.glide.Glide
import com.edvora.mobiletest.databinding.ActivityMainBinding
import com.edvora.mobiletest.databinding.DialogFilterBinding
import com.edvora.mobiletest.model.City
import com.edvora.mobiletest.model.State
import com.edvora.mobiletest.ui.adapter.RiderAdapter
import com.edvora.mobiletest.ui.fragment.RideFragment
import com.edvora.mobiletest.ui.viewmodel.MainViewModel
import com.edvora.mobiletest.utils.fetchAndDisplayImage
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var filterPopup: PopupWindow

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater).apply {
            setContentView(root)

            viewModel.userObserver.observe(this@MainActivity){
                textProfileName.text = it.name
                imageProfile.fetchAndDisplayImage(it.profileKey)

                //everything to be displayed depends on the user being shown
                viewModel.userStationCode = it.stationCode
                setMainContent()
            }

            viewModel.rideObserver.observe(this@MainActivity){
                setPopupSpinnerData(viewModel.getAllStates(), viewModel.getAllCities())
                viewModel.rideObserver.removeObservers(this@MainActivity)
            }
        }

        viewModel.getUsers()
    }

    private fun setMainContent() {
        binding.apply {

            val adapter = RidePagerAdapter(this@MainActivity)
            viewPager.adapter = adapter

            TabLayoutMediator(tabLayout, viewPager){tab, position ->
                tab.text = adapter.getTitle(position)
            }.attach()

            buttonFilter.setOnClickListener {
                filterPopup.showAsDropDown(it)
            }

            setFilterPopup()

            binding.tabLayout.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener{
                override fun onTabSelected(tab: TabLayout.Tab?) {
//                    tab?.let { changeTabTextAppearance(it, true) }
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {
//                    tab?.let { changeTabTextAppearance(it, false) }
                }

                override fun onTabReselected(tab: TabLayout.Tab?) {}
            })
        }
    }

    private fun changeTabTextAppearance(tab: TabLayout.Tab, toSelected: Boolean){
        val style = if(toSelected) R.style.TextAppearance_Tab_Selected else R.style.TextAppearance_Tab

        tab.let {
            val linearLayout = ((binding.tabLayout.getChildAt(0) as ViewGroup).getChildAt(it.position) as LinearLayout)
            val textView = linearLayout.getChildAt(1) as TextView

            TextViewCompat.setTextAppearance(textView, style)
        }
    }

    private fun setFilterPopup(){
        val contentView = layoutInflater.inflate(R.layout.dialog_filter, null, false)
        val width = LinearLayout.LayoutParams.WRAP_CONTENT
        filterPopup = PopupWindow(contentView, width, width, true)
    }

    private fun setPopupSpinnerData(states: List<State>, city: List<City>){
        val stateSpinner = filterPopup.contentView.findViewById<Spinner>(R.id.spinner_state)
        val stateAdapter = ArrayAdapter<String>(this, R.layout.spinner_textview)
        stateAdapter.add(getString(R.string.all))
        stateAdapter.addAll(states.map { it.name })
        stateSpinner.adapter = stateAdapter
        stateSpinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if(view != null){
                    val state = stateAdapter.getItem(position) ?: ""
                    viewModel.currentState = if(state == getString(R.string.all)) "" else state
                    viewModel.getRides()
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }

        val citySpinner = filterPopup.contentView.findViewById<Spinner>(R.id.spinner_city)
        val cityAdapter = ArrayAdapter<String>(this, R.layout.spinner_textview)
        cityAdapter.add(getString(R.string.all))
        cityAdapter.addAll(city.map { it.name })
        citySpinner.adapter = cityAdapter
        citySpinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if(view != null){
                    val city = cityAdapter.getItem(position) ?: ""
                    viewModel.currentCity = if(city == getString(R.string.all)) "" else city
                    viewModel.getRides()
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }
    }

    class RidePagerAdapter(activity: FragmentActivity): FragmentStateAdapter(activity) {
        private val pageTitles = activity.resources.getStringArray(R.array.tag_names)

        override fun getItemCount() = pageTitles.size

        override fun createFragment(position: Int): Fragment {
            val tab = when(position) {
                0 -> RideFragment.KEY_NEWEST
                1 -> RideFragment.KEY_UPCOMING
                else -> RideFragment.KEY_PAST
            }

            return RideFragment.getInstance(tab)
        }

        fun getTitle(position: Int) = pageTitles[position]

    }
}