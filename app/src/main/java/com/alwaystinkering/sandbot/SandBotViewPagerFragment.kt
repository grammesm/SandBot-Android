package com.alwaystinkering.sandbot

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.alwaystinkering.sandbot.adapters.BOT_PAGE_INDEX
import com.alwaystinkering.sandbot.adapters.FILES_PAGE_INDEX
import com.alwaystinkering.sandbot.adapters.SandBotPagerAdapter
import com.alwaystinkering.sandbot.databinding.FragmentViewPagerBinding
import com.google.android.material.tabs.TabLayoutMediator


class SandBotViewPagerFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentViewPagerBinding.inflate(inflater, container, false)
        val tabLayout = binding.tabs
        val viewPager = binding.viewPager

        viewPager.adapter = SandBotPagerAdapter(this)

        // Set the icon and text for each tab
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            //tab.setIcon(getTabIcon(position))
            tab.text = getTabTitle(position)
        }.attach()

        (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)

        return binding.root
    }

//    private fun getTabIcon(position: Int): Int {
//        return when (position) {
//            BOT_PAGE_INDEX -> R.drawable.garden_tab_selector
//            FILES_PAGE_INDEX-> R.drawable.plant_list_tab_selector
//            else -> throw IndexOutOfBoundsException()
//        }
//    }

    private fun getTabTitle(position: Int): String? {
        return when (position) {
            BOT_PAGE_INDEX -> getString(R.string.tab_text_bot)
            FILES_PAGE_INDEX -> getString(R.string.tab_text_files)
            else -> null
        }
    }
}