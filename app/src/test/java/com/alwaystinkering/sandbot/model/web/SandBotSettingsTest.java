package com.alwaystinkering.sandbot.model.web;

import com.alwaystinkering.sandbot.model.pattern.ParametricPattern;

import junit.framework.TestCase;

import org.junit.Test;

public class SandBotSettingsTest extends TestCase {

    private SandBotSettings sandBotSettings;
    private ParametricPattern parametricPattern = new ParametricPattern("myPattern", "x = 0", "x = sin(x);\ny = cos(x)\n");

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
        sandBotSettings.getPatterns().put(parametricPattern.getName(), parametricPattern);
        System.out.println(sandBotSettings.toJson());
        assertTrue(true);
    }
}