package com.alwaystinkering.sandbot.ui.sandbot;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class TabPagerAdapter extends FragmentPagerAdapter {

    private static final int TAB_BOT = 0;
    private static final int TAB_PATTERN = 1;
    private static final int TAB_SEQUENCE = 2;

    private BotFragment botFragment;
    private PatternFragment patternFragment;
    private SequenceFragment sequenceFragment;

    private boolean enabled = true;

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
        if (sequenceFragment != null) {
            sequenceFragment.refresh();
        }
    }

    public void disable() {
        if (botFragment != null) {
            botFragment.disable();
        }
        if (patternFragment != null) {
            patternFragment.disable();
        }
        if (sequenceFragment != null) {
            sequenceFragment.disable();
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
        if (sequenceFragment != null) {
            sequenceFragment.enable();
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
            case TAB_PATTERN:
                return patternFragment = new PatternFragment();
            case TAB_SEQUENCE:
                return sequenceFragment = new SequenceFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        // Show 3 total pages.
        return 3;
    }
}
