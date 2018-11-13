package com.alwaystinkering.sandbot.model.state;

import com.alwaystinkering.sandbot.model.web.SandBotSettings;
import com.alwaystinkering.sandbot.model.web.SandBotStatus;

public class SandBotStateManager {

    private static SandBotSettings sandBotSettings = new SandBotSettings();
    private static SandBotStatus sandBotStatus = new SandBotStatus();

    public static SandBotSettings getSandBotSettings() {
        return sandBotSettings;
    }

    public static SandBotStatus getSandBotStatus() {
        return sandBotStatus;
    }
}
