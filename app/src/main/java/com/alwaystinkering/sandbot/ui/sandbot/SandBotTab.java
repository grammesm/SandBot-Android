package com.alwaystinkering.sandbot.ui.sandbot;

import android.support.v4.app.Fragment;

public abstract class SandBotTab extends Fragment {
    abstract void refresh();
    abstract void enable();
    abstract void disable();
}
