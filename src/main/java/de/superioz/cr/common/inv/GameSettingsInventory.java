package de.superioz.cr.common.inv;

import de.superioz.cr.common.WrappedGamePlayer;
import de.superioz.cr.common.game.Game;
import de.superioz.cr.common.settings.GameSettings;
import de.superioz.library.bukkit.common.inventory.InventorySize;
import de.superioz.library.bukkit.common.inventory.SuperInventory;
import de.superioz.library.bukkit.common.item.SimpleItem;
import org.bukkit.DyeColor;
import org.bukkit.Material;

/**
 * This class was created as a part of CastleRush
 *
 * @author Superioz
 */
public class GameSettingsInventory {

    private static SuperInventory superInventory;

    /**
     * Open the settings inventory
     *
     * @param player The player
     */
    public static void openGameSettings(WrappedGamePlayer player){
        Game game = player.getGame();
        GameSettings settings = game.getSettings();

        // Check exist
        if(superInventory != null)
            superInventory = new SuperInventory("Setup the game", InventorySize.FIVE_ROWS);
    }

    private static void initItems(GameSettings settings){
        SimpleItem buildTimeItem = new SimpleItem(Material.WATCH).setName("&6Build Time | " + settings.getBuildTimer() + " minute(s)");
        SimpleItem rankedMatch = getBooleanItem("&6Ranked Match", settings.isRankedMatch());
        SimpleItem enterPlot = getBooleanItem("&6Enter Plot during Buildphase", settings.canEnterPlotDuringBuild());
        SimpleItem pvp = getBooleanItem("PvP", settings.isPvp());
    }

    private static SimpleItem getBooleanItem(String name, boolean b){
        return new SimpleItem(Material.WOOL).setName(name).setColor(b ? DyeColor.GREEN : DyeColor.RED)
                .addLore("&7Click to " + (b ? "deactivate" : "activate"));
    }


    // -- Items

    private static final SimpleItem TIME_ADD_BUTTON = new SimpleItem(Material.WOOD_BUTTON)
            .setName("&aAdd time to countdown")
            .addLore("&7Normal click to add &f1 &7minute",
                    "&7Shift-Click to add &f5 &7minutes");
    private static final SimpleItem TIME_REMOVE_BUTTON = new SimpleItem(Material.WOOD_BUTTON)
            .setName("&cRemove time from countdown")
            .addLore("&7Normal click to remove 1 minute",
                    "&7Shift-Click to remove 5 minutes");

}
