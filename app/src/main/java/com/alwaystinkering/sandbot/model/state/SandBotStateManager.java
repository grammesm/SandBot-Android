package com.alwaystinkering.sandbot.model.state;

import com.alwaystinkering.sandbot.model.web.SandBotSettings;

public class SandBotStateManager {

    private static SandBotSettings sandBotSettings = new SandBotSettings();

    public static SandBotSettings getSandBotSettings() {
        return sandBotSettings;
    }
}
