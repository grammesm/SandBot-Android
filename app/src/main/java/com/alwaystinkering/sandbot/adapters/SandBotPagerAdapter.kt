package com.alwaystinkering.sandbot.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.alwaystinkering.sandbot.ui.BotFragment
import com.alwaystinkering.sandbot.ui.FilesFragment

const val BOT_PAGE_INDEX = 0
const val FILES_PAGE_INDEX = 1


/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
class SandBotPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    /**
     * Mapping of the ViewPager page indexes to their respective Fragments
     */
    private val tabFragmentsCreators: Map<Int, () -> Fragment> = mapOf(
        BOT_PAGE_INDEX to { BotFragment() },
        FILES_PAGE_INDEX to { FilesFragment() }
    )

    override fun getItemCount() = tabFragmentsCreators.size

    override fun createFragment(position: Int): Fragment {
        return tabFragmentsCreators[position]?.invoke() ?: throw IndexOutOfBoundsException()
    }
}