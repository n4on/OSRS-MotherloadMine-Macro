package net.runelite.client.plugins.microbot.MLMNeon;

import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.MLMNeon.enums.MLMNeonStates;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.equipment.Rs2Equipment;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.concurrent.TimeUnit;

public class MLMNeonScript extends Script {
    public static double version = 1.0;
    public static MLMNeonStates state;
    public static String pickaxe;
    public static int pickaxeMiningID;
    public static String hammer = "Hammer";

    public static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
    public String timestamp = LocalDateTime.now().format(formatter);

    WorldPoint[] startingPosition = new WorldPoint[] {new WorldPoint(3733, 5674, 0), new WorldPoint(3749, 5672, 0), new WorldPoint(3760, 5666, 0), new WorldPoint(3742, 5663, 0)};
    
    public boolean run(MLMNeonConfig config) {
        state = config.botState();
        pickaxe = config.pickaxeType().name();
        pickaxeMiningID = config.pickaxeType().getId();

        Microbot.enableAutoRunOn = true;
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            if (!super.run()) return;
            try {
                if (!Microbot.hasLevel(30, Skill.CRAFTING)) {
                    Microbot.showMessage("Mining level too low to enter Motherload mine!!");
                    shutdown();
                    return;
                }

                switch (state) {
                    case bank:
                        collectFromBank();
                        break;
                    case goToMining:
                        goToMiningLocation();
                        break;
                    case mining:
                        mining();
                        break;
                    case fixWheel:
                        fixingWheel();
                        break;
                    case droppingOffItems:
                        dropOffItems();
                        break;
                    case collectingSack:
                        collectingSack();
                        break;
                    default:
                        break;
                }

            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }, 0, 100, TimeUnit.MILLISECONDS);
        return true;
    }

    public void collectFromBank() {
        if (Rs2Inventory.hasItem(hammer) && Rs2Equipment.hasEquippedContains(pickaxe)) {
            state = MLMNeonStates.mining;

        } else if (!Rs2Inventory.hasItem(hammer)) {
            if (!Rs2Bank.hasItem(hammer)) {
                Microbot.showMessage("You don't have a Hammer!");
                shutdown();
            }
            if (!Rs2Bank.isOpen()) {
                Rs2Bank.useBank();
                System.out.println("Used Bank");
            }

            Rs2Bank.withdrawItem(hammer);

        } else if (!Rs2Equipment.hasEquippedContains(pickaxe)) {
            Microbot.showMessage("You don't have a pickaxe equipped!");
            shutdown();
        }
    }
    public void goToMiningLocation() {
        state = MLMNeonStates.doing;

        runDefault();

        Microbot.getWalker().walkFastLocal(LocalPoint.fromWorld(Microbot.getClient(), startingPosition[0]));

        System.out.println("\u001B[95m[" + LocalDateTime.now().format(formatter) + "] Distance to position = " + Microbot.getClient().getLocalPlayer().getWorldLocation().distanceTo(startingPosition[0]) + "\u001B[0m");

        sleepUntil(() -> Rs2Player.getWorldLocation().distanceTo(startingPosition[0]) == 0, 5_000);

        System.out.println("\u001B[95m[" + LocalDateTime.now().format(formatter) + "] At Location \u001B[0m");

        state = MLMNeonStates.mining;
    }
    public void mining() {
        state = MLMNeonStates.doing;

        mineVein();
        System.out.println("\u001B[95m[" + LocalDateTime.now().format(formatter) + "] Mining check \u001B[0m");

        sleepUntil(() -> Microbot.getClient().getLocalPlayer().getAnimation() == pickaxeMiningID, 3_000);

        if (Rs2Inventory.isFull()) {
            System.out.println("\u001B[95m[" + LocalDateTime.now().format(formatter) + "] Inventory full \u001B[0m");
            state = MLMNeonStates.fixWheel;
        } else {
            state = MLMNeonStates.mining;
        }
    }
    public void fixingWheel() {
        if (Rs2GameObject.findObjectById(2016) != null) {
            System.out.println("\u001B[95m[" + LocalDateTime.now().format(formatter) + "] Strust doesn't need fixing \u001B[0m");
            Microbot.getWalker().hybridWalkTo(new WorldPoint(3749, 5658, 0), false, true);
            state = MLMNeonStates.droppingOffItems;
        } else {
            System.out.println("\u001B[95m[" + LocalDateTime.now().format(formatter) + "] Fixing strut \u001B[0m");

            Microbot.getWalker().walkFastLocal(LocalPoint.fromWorld(Microbot.getClient(), startingPosition[3]));

            Rs2GameObject.interact(ObjectID.BROKEN_STRUT);

            sleepUntil(()-> Rs2GameObject.interact(ObjectID.BROKEN_STRUT), 15_000);
        }
    }
    public void dropOffItems() {
        state = MLMNeonStates.doing;

        Microbot.getWalker().walkFastLocal(LocalPoint.fromWorld(Microbot.getClient(), startingPosition[1]));
        sleepUntil(() -> Rs2Player.getWorldLocation().distanceTo(startingPosition[1]) < 10, 10_000);

        System.out.println("\u001B[95m[" + LocalDateTime.now().format(formatter) + "] Distance to position = " + Microbot.getClient().getLocalPlayer().getWorldLocation().distanceTo(startingPosition[1]) + "\u001B[0m");

        GameObject hopper = Rs2GameObject.findObject("Hopper");
        Rs2GameObject.interact(hopper, "Deposit");

        sleepUntil(() -> !Rs2Inventory.hasItem("Pay-dirt"), 20_000);

        System.out.println("\u001B[95m[" + LocalDateTime.now().format(formatter) + "] Deposited \u001B[0m");

        if (Microbot.getVarbitValue(Varbits.SACK_NUMBER) >= 52) {
            var sackAmount = Microbot.getVarbitValue(Varbits.SACK_NUMBER);
            sleepUntil(()-> sackAmount != Microbot.getVarbitValue(Varbits.SACK_NUMBER), 10_000);

            state = MLMNeonStates.collectingSack;
        } else {
            runDefault();

            state = MLMNeonStates.goToMining;
        }
    }
    public void collectingSack() {
        state = MLMNeonStates.doing;

        System.out.println("\u001B[95m[" + LocalDateTime.now().format(formatter) + "] Searching Sack \u001B[0m");
        Rs2GameObject.interact(26688);

        sleepUntil(Rs2Inventory::isFull, 6_000);

        System.out.println("\u001B[95m[" + LocalDateTime.now().format(formatter) + "] Using bank \u001B[0m");
        Microbot.getWalker().walkFastLocal(LocalPoint.fromWorld(Microbot.getClient(), startingPosition[2]));

        Rs2Bank.useBank();
        var openedBank = sleepUntil(Rs2Bank::isOpen, 10_000);
        if (!openedBank) collectingSack();

        System.out.println("\u001B[95m[" + LocalDateTime.now().format(formatter) + "] Depositing Items \u001B[0m");
        Rs2Bank.depositAll();
        sleepUntil(Rs2Inventory::isEmpty, 10_000);

        Rs2Bank.withdrawItem(hammer);

        if (Microbot.getVarbitValue(Varbits.SACK_NUMBER) != 0) {
            state = MLMNeonStates.collectingSack;
        } else {
            state = MLMNeonStates.goToMining;
        }
    }
    public void mineVein() {
        WallObject closest = Rs2GameObject.getWallObjects()
                .stream()
                .filter(x -> x.getId() == 26661 || x.getId() == 26662 || x.getId() == 26663 || x.getId() == 26664)
                .sorted(Comparator.comparingInt(x -> Microbot.getClient().getLocalPlayer().getWorldLocation().distanceTo(x.getWorldLocation())))
                .filter(x -> Microbot.getWalker().canInteract(x.getWorldLocation()))
                .findFirst()
                .orElse(null);

        if (closest == null) return;

        Rs2GameObject.interact(closest);

        sleepUntil(()-> Microbot.getClient().getLocalPlayer().getAnimation() == AnimationID.IDLE, 20_000);
    }

    public void runDefault() {
        System.out.println("\u001B[95m[" + LocalDateTime.now().format(formatter) + "] Distance to position = " + Microbot.getClient().getLocalPlayer().getWorldLocation().distanceTo(startingPosition[0]) + "\u001B[0m");

        Microbot.getWalker().walkFastLocal(LocalPoint.fromWorld(Microbot.getClient(), new WorldPoint(3757, 5663, 0)));

        sleepUntil(() -> Rs2Player.getWorldLocation().distanceTo(new WorldPoint(3757, 5663, 0)) == 0, 5_000);
    }
}
