package net.runelite.client.plugins.microbot.MLMNeon;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.microbot.MLMNeon.enums.MLMNeonStates;
import net.runelite.client.ui.overlay.OverlayManager;
import javax.inject.Inject;
import java.awt.*;

@PluginDescriptor(
        name = PluginDescriptor.Default + "MLMNeon",
        description = "Motherload Mine Macro.",
        tags = {"Motherload", "microbot"},
        enabledByDefault = false
)
@Slf4j
public class MLMNeonPlugin extends Plugin {
    @Inject
    private MLMNeonConfig config;

    @Provides
    MLMNeonConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(MLMNeonConfig.class);
    }

    @Inject
    private OverlayManager overlayManager;
    @Inject
    private MLMNeonOverlay MLMNeonOverlay;

    @Inject
    MLMNeonScript MLMNeonScript;

    @Override
    protected void startUp() throws AWTException {
        if (overlayManager != null) {
            overlayManager.add(MLMNeonOverlay);
        }
        MLMNeonScript.run(config);
    }

    protected void shutDown() {
        MLMNeonScript.shutdown();
        overlayManager.remove(MLMNeonOverlay);
    }
}
