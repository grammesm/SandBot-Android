package com.alwaystinkering.sandbot.ui.sandbot;

import androidx.fragment.app.Fragment;

public abstract class SandBotTab extends Fragment {
    abstract void refresh();
    abstract void enable();
    abstract void disable();
}
