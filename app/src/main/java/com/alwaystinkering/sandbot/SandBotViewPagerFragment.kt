package com.alwaystinkering.sandbot

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.alwaystinkering.sandbot.adapters.BOT_PAGE_INDEX
import com.alwaystinkering.sandbot.adapters.FILES_PAGE_INDEX
import com.alwaystinkering.sandbot.adapters.SandBotPagerAdapter
import com.alwaystinkering.sandbot.databinding.FragmentViewPagerBinding
import com.google.android.material.tabs.TabLayoutMediator


class SandBotViewPagerFragment : Fragment() {

    lateinit var binding: FragmentViewPagerBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentViewPagerBinding.inflate(inflater, container, false)
        val tabLayout = binding.tabs
        val viewPager = binding.viewPager

        val titleText = binding.toolbarText

        (activity as AppCompatActivity?)!!.setSupportActionBar(binding.toolbar)
        titleText.setOnTouchListener(View.OnTouchListener { view, motionEvent ->
            if (motionEvent.getRawX() >= titleText.getRight() - titleText.getTotalPaddingRight()) navigateToSettings(view)
            return@OnTouchListener true
        })

        viewPager.adapter = SandBotPagerAdapter(this)

        // Set the icon and text for each tab
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            //tab.setIcon(getTabIcon(position))
            tab.text = getTabTitle(position)
        }.attach()

        (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)

        return binding.root
    }

    private fun navigateToSettings(
        view: View
    ) {
        val direction =
            SandBotViewPagerFragmentDirections.actionHomeViewPagerFragmentToSettingsFragment()
        view.findNavController().navigate(direction)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main, menu);
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId ){
            R.id.menu_settings -> navigateToSettings(item.actionView)
        }
        return super.onOptionsItemSelected(item)
    }

    private fun getTabTitle(position: Int): String? {
        return when (position) {
            BOT_PAGE_INDEX -> getString(R.string.tab_text_bot)
            FILES_PAGE_INDEX -> getString(R.string.tab_text_files)
            else -> null
        }
    }
}