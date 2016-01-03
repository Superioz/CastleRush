package de.superioz.cr.util;

import de.superioz.cr.common.lang.LanguageManager;
import de.superioz.cr.common.game.GameType;
import de.superioz.library.java.util.SimpleStringUtils;
import de.superioz.library.minecraft.server.common.item.SimpleItem;
import de.superioz.library.minecraft.server.common.item.SimpleItemSpell;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;

/**
 * This class was created as a part of CastleRush
 *
 * @author Superioz
 */
public class PluginItems {
    //TODO write to properties

    public static final SimpleItem MULTITOOL_STACK = new SimpleItem(Material.DIAMOND_BARDING)
            .setAmount(1).setUnbreakable(true)
            .setFlags(true, ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ATTRIBUTES)
            .setName(LanguageManager.get("multitoolMainTitle")).setLore(LanguageManager.get("multitoolMainLore"));

    public static final SimpleItem MULTITOOL_STACK_SHOVEL = new SimpleItem(Material.DIAMOND_SPADE)
            .setAmount(1).setUnbreakable(true)
            .setFlags(true, ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ATTRIBUTES)
            .setName(LanguageManager.get("multitoolShovelTitle")).setLore(LanguageManager.get("multitoolShovelLore"));

    public static final SimpleItem MULTITOOL_STACK_PICKAXE = new SimpleItem(Material.DIAMOND_PICKAXE)
            .setAmount(1).setUnbreakable(true)
            .setFlags(true, ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ATTRIBUTES)
            .setName(LanguageManager.get("multitoolPickaxeTitle")).setLore(LanguageManager.get("multitoolPickaxeLore"));

    public static final SimpleItem MULTITOOL_STACK_HOE = new SimpleItem(Material.DIAMOND_HOE)
            .setAmount(1).setUnbreakable(true)
            .setFlags(true, ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ATTRIBUTES)
            .setName(LanguageManager.get("multitoolHoeTitle")).setLore(LanguageManager.get("multitoolHoeLore"));

    public static final SimpleItem TEAM_CHOOSE_TOOL = new SimpleItem(Material.NETHER_STAR)
            .setAmount(1).enchant(new SimpleItemSpell(Enchantment.ARROW_DAMAGE, 1))
            .setFlags(true, ItemFlag.HIDE_ENCHANTS)
            .setName(LanguageManager.get("teamChooseToolTitle")).setLore(LanguageManager.get("teamChooseToolLore"));

    public static final SimpleItem GAMETYPE_PRIVATE = new SimpleItem(Material.STAINED_CLAY)
            .setColor(GameType.PRIVATE.getColor())
            .setName("&c" + SimpleStringUtils.upperFirstLetter(GameType.PRIVATE.name().toLowerCase()))
            .addLore("&7Click to create a " + GameType.PRIVATE.name().toLowerCase() + " &7game",
                    "&7Where the game runs manual",
                    "&7And the game master set up everything");

    public static final SimpleItem GAMETYPE_PUBLIC = new SimpleItem(Material.STAINED_CLAY)
            .setColor(GameType.PUBLIC.getColor())
            .setName("&9" + SimpleStringUtils.upperFirstLetter(GameType.PUBLIC.name().toLowerCase()))
            .addLore("&7Click to create a " + GameType.PUBLIC.name().toLowerCase() + " &7game",
                    "&7Where the game runs automatic",
                    "&7And the config.yml set up everything");

    public static final SimpleItem INGAME_TELEPORT_TOOL = new SimpleItem(Material.WATCH)
            .setName("&bTeleport Tool").addLore("&7Rightclick to open the teleport overview");

    public static final SimpleItem TELEPORT_TOOL_PLOTS = new SimpleItem(Material.GRASS)
            .setName("&6Plot").addLore("&7Click to choose a plot to teleport");

    public static final SimpleItem TELEPORT_TOOL_CHECKPOINTS = new SimpleItem(Material.IRON_PLATE)
            .setName("&6Checkpoint").addLore("&7Click to choose a checkpoint to teleport");

    public static final SimpleItem TELEPORT_TOOL_MATE = new SimpleItem(Material.SKULL_ITEM, 1, (short) 3)
            .setName("&6Team Mate").addLore("&7Click to choose a mate to teleport");

    public static final SimpleItem MIDDLE_PAGE_CHOOSE_ARENA = new SimpleItem(Material.BOOK)
            .setName("&fPage information").addLore("&7Click an arena from this page to create a game");

    public static final SimpleItem MIDDLE_PAGE_GAME_VIEW = new SimpleItem(Material.BOOK)
            .setName("&fPage information").addLore("&7Just an overview of all running games");

    public static final SimpleItem MIDDLE_PAGE_GAME_MATES = new SimpleItem(Material.BOOK)
            .setName("&fPage information").addLore("&7Choose one of your teammate to teleport");

    public static final SimpleItem NEXT_PAGE = new SimpleItem(Material.ARROW)
            .setName("&aNext page").addLore("&7Click to get to the next page");

    public static final SimpleItem LAST_PAGE = new SimpleItem(Material.ARROW)
            .setName("&cNext page").addLore("&7Click to get to the last page");

    public static final SimpleItem GAMEMASTER_SETTINGS_TOOL = new SimpleItem(Material.BOOK)
            .setName("&5Game Master Settings").addLore("&7Rightclick to open settings menu")
            .setAmount(1).enchant(new SimpleItemSpell(Enchantment.ARROW_DAMAGE, 1))
            .setFlags(true, ItemFlag.HIDE_ENCHANTS);

}
