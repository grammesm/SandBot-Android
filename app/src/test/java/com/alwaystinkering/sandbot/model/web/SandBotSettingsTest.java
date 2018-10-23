package com.alwaystinkering.sandbot.model.web;

import com.alwaystinkering.sandbot.model.pattern.Pattern;

import junit.framework.TestCase;

import org.junit.Test;

public class SandBotSettingsTest extends TestCase {

    private SandBotSettings sandBotSettings;
    private Pattern pattern = new Pattern("myPattern", "x = 0", "x = sin(x);\ny = cos(x)\n");

    @Override
    public void setUp() throws Exception {
        super.setUp();
        sandBotSettings = new SandBotSettings();
        sandBotSettings.setMaxCfgLen(2000);
        sandBotSettings.setName("Table Name");
        sandBotSettings.setStartup("");
    }

    @Test
    public void testToJson() {
        sandBotSettings.getPatterns().put(pattern.getName(), pattern);
        System.out.println(sandBotSettings.toJson());
        assertTrue(true);
    }
}