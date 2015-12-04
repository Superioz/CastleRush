package de.superioz.cr.util;

import de.superioz.cr.main.CastleRush;
import de.superioz.library.minecraft.server.common.command.CommandHandler;
import de.superioz.library.minecraft.server.common.item.SimpleItem;
import de.superioz.library.minecraft.server.message.BukkitChat;
import de.superioz.library.minecraft.server.util.ChatUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;

import java.util.Arrays;
import java.util.List;

/**
 * This class was created as a part of CastleRush (Spigot)
 *
 * @author Superioz
 */
public class Utilities {

    public static CommandHelpPage commandHelpPage;

    static {

        commandHelpPage = new CommandHelpPage(10, CommandHandler.getCommands());

    }

    public static String getSpacer(String middle){
        return ChatUtil.colored("&8===========[ &b" + middle + "&r &8]===========");
    }

    public static String getListItem(String item){
        return ChatUtil.colored("&8# " + item);
    }

    public static void getPluginInformationPage(Player player){
        List<String> str = Arrays.asList(
                getSpacer("CastleRush"),
                getListItem("&7Author: &b" + CastleRush.getInstance().getDescription().getAuthors().get(0)),
                getListItem("&7Commands: &b" + CommandHandler.getAllCommands().size()),
                getListItem("&7Version: &b" + CastleRush.getInstance().getDescription().getVersion()),
                getSpacer("CastleRush")
        );

        for(String s : str)
            BukkitChat.send(s, player);
    }

    public static boolean materialExist(String name){
        return Material.getMaterial(name) != null;
    }


    public static class ItemStacks {

        public static final SimpleItem MULTITOOL_STACK = new SimpleItem(Material.DIAMOND_BARDING)
                .setAmount(1).setUnbreakable(true)
                .setFlags(true, ItemFlag.HIDE_UNBREAKABLE)
                .setName("&6Multitool - 3 in 1").setLore("&7Sneak-Rightclick to change tool");

        public static final SimpleItem MULTITOOL_STACK_SHOVEL = new SimpleItem(Material.DIAMOND_SPADE)
                .setAmount(1).setUnbreakable(true)
                .setFlags(true, ItemFlag.HIDE_UNBREAKABLE)
                .setName("&6Multitool - 3 in 1").setLore("&7Rightclick: Area pos1; Leftclick: Area pos2;").setLore
                        ("&7Sneak+Leftclick: Single point");

        public static final SimpleItem MULTITOOL_STACK_PICKAXE = new SimpleItem(Material.DIAMOND_PICKAXE)
                .setAmount(1).setUnbreakable(true)
                .setFlags(true, ItemFlag.HIDE_UNBREAKABLE)
                .setName("&6Multitool - 3 in 1").setLore("&7Rightclick: Set pos1; Leftclick: Set pos2");

        public static final SimpleItem MULTITOOL_STACK_HOE = new SimpleItem(Material.DIAMOND_HOE)
                .setAmount(1).setUnbreakable(true)
                .setFlags(true, ItemFlag.HIDE_UNBREAKABLE)
                .setName("&6Multitool - 3 in 1").setLore("&7Rightclick: Add spawnpoint; Leftclick: Remove one");

        // ========================================================================================================

        public static final SimpleItem EDITOR_CACHE_CLOSE_INV_ITEM = new SimpleItem(Material.BARRIER)
                .setName("&cClose Inventory").setLore("&7&cClick to close the inventory");

        public static final SimpleItem EDITOR_CACHE_INVENTORY_INFO = new SimpleItem(Material.BOOK)
                .setName("&9Editor Cache Inventory");

        public static final SimpleItem EDITOR_CACHE_BACK = new SimpleItem(Material.ARROW)
                .setName("&eGet back").setLore("&7&cClick to get back to the inventory");

        public static final SimpleItem EDITOR_CACHE_SPAWNPOINTS = new SimpleItem(Material.ENDER_PEARL)
                .setName("&dSpawnpoints").setLore("&7&cClick to overview all spawnpoints");

        public static final SimpleItem EDITOR_CACHE_PLOTS = new SimpleItem(Material.GRASS)
                .setName("&dPlots").setLore("&7&cClick to overview all plots");

        public static final SimpleItem EDITOR_CACHE_WALLS = new SimpleItem(Material.BRICK)
                .setName("&dWalls").setLore("&7&cClick to overview all walls");

    }

}
