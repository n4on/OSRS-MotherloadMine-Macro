package net.runelite.client.plugins.microbot.MLMNeon;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.plugins.microbot.MLMNeon.enums.MLMNeonPickaxe;
import net.runelite.client.plugins.microbot.MLMNeon.enums.MLMNeonStates;

@ConfigGroup("idefk")
public interface MLMNeonConfig extends Config {
    String GROUP = "MLM";

    @ConfigItem(
            keyName = "guide",
            name = "How to use",
            description = "How to use this plugin",
            position = 0
    )
    default String GUIDE() { return "Requirements - Hold any pickaxe - Have hammer in Inv/ Bank. Stand in Motherload Mine and start script."; }

    @ConfigSection(
            name = "General",
            description = "General",
            position = 1
    )
    String generalSection = "general";

    @ConfigItem(
            keyName = "State",
            name = "State",
            description = "Choose bot state.",
            position = 0,
            section = generalSection
    )
    default MLMNeonStates botState()
    {
        return MLMNeonStates.bank;
    }

    @ConfigSection(
            name = "Pickaxe",
            description = "Pick your pickaxe.",
            position = 0
    )
    String pickaxeSection = "pickaxe";

    @ConfigItem(
            keyName = "Pickaxe",
            name = "Pickaxe",
            description = "Choose pickaxe.",
            position = 0,
            section = pickaxeSection
    )
    default MLMNeonPickaxe pickaxeType()
    {
        return MLMNeonPickaxe.NONE;
    }
}