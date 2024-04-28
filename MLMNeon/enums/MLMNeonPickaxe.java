package net.runelite.client.plugins.microbot.MLMNeon.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.api.AnimationID;

@Getter
@RequiredArgsConstructor
public enum MLMNeonPickaxe {
    NONE("NONE", AnimationID.IDLE),
    BRONZE("Bronze pickaxe", AnimationID.MINING_MOTHERLODE_BRONZE),
    IRON("Iron pickaxe", AnimationID.MINING_MOTHERLODE_IRON),
    STEEL("Steel pickaxe", AnimationID.MINING_MOTHERLODE_STEEL),
    BLACK("Black pickaxe", AnimationID.MINING_MOTHERLODE_BLACK),
    MITHRIL("Mithril pickaxe", AnimationID.MINING_MOTHERLODE_MITHRIL),
    ADAMANT("Adamant pickaxe", AnimationID.MINING_MOTHERLODE_ADAMANT),
    RUNE("Rune pickaxe", AnimationID.MINING_MOTHERLODE_RUNE),
    DRAGON("Dragon pickaxe", AnimationID.MINING_MOTHERLODE_DRAGON),
    CRYSTAL("Crystal pickaxe", AnimationID.MINING_MOTHERLODE_CRYSTAL);

    private final String name;
    private final int id;

    @Override
    public String toString()
    {
        return name;
    }
}
