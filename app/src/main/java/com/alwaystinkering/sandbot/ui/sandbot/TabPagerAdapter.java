package com.alwaystinkering.sandbot.ui.sandbot;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class TabPagerAdapter extends FragmentPagerAdapter {

    private static final int TAB_BOT = 0;
    private static final int TAB_FILES = 1;

    private BotFragment botFragment;
    private PatternFragment patternFragment;

    private boolean enabled = false;

    public TabPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public void refresh() {
        if (botFragment != null) {
            botFragment.refresh();
        }
        if (patternFragment != null) {
            patternFragment.refresh();
        }
    }

    public void disable() {
        if (botFragment != null) {
            botFragment.disable();
        }
        if (patternFragment != null) {
            patternFragment.disable();
        }
        enabled = false;
    }

    public void enable() {
        if (botFragment != null) {
            botFragment.enable();
        }
        if (patternFragment != null) {
            patternFragment.enable();
        }
        enabled = true;
    }

    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        switch (position) {
            case TAB_BOT:
                return botFragment = new BotFragment();
            case TAB_FILES:
                return patternFragment = new PatternFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        // Show 2 total pages.
        return 2;
    }
}
