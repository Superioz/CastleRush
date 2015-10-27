package de.superioz.cr.util;

import de.superioz.cr.main.CastleRush;
import de.superioz.library.minecraft.server.command.CommandHandler;
import de.superioz.library.minecraft.server.command.help.CommandHelpPage;
import de.superioz.library.minecraft.server.command.help.CommandHelpPattern;
import de.superioz.library.minecraft.server.items.ItemBuilder;
import de.superioz.library.minecraft.server.util.chat.BukkitChat;
import de.superioz.library.minecraft.server.util.chat.ChatUtils;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class was created as a part of CastleRush (Spigot)
 *
 * @author Superioz
 */
public class Utilities {

    public static CommandHelpPage commandHelpPage;

    public static String getSpacer(String middle){
        return ChatUtils.colored("&8===========[ &b" + middle + "&r &8]===========");
    }

    public static String getListItem(String item){
        return ChatUtils.colored("&8# " + item);
    }

    public static void getPluginInformationPage(Player player){
        List<String> str = Arrays.asList(
                getSpacer("CastleRush"),
                getListItem("&7Author: &b" + CastleRush.getInstance().getDescription().getAuthors().get(0)),
                getListItem("&7Commands: &b" + CommandHandler.size()),
                getListItem("&7Version: &b" + CastleRush.getInstance().getDescription().getVersion()),
                getSpacer("CastleRush")
        );

        for(String s : str)
            BukkitChat.send(s, player);
    }

    public static void initCommandHelp(String nextPageCommand){
        if(commandHelpPage == null){
            commandHelpPage = new CommandHelpPage(new CommandHelpPattern(
                    12, CastleRush.getProperties().get("helpCommandHover"),
                    getListItem(CastleRush.getProperties().get("helpCommandListItem")),
                    getListItem(CastleRush.getProperties().get("helpCommandNextPage").replace("%label", nextPageCommand)),
                    CastleRush.getProperties().get("helpCommandHover"),
                    CastleRush.getProperties().get("helpCommandNextPageHover"),
                    getListItem(CastleRush.getProperties().get("helpCommandListItem"))
            ));
        }
    }

    public static List<TextComponent> getCommandHelp(String command, int page){
        List<TextComponent> l = new ArrayList<>();

        if(!commandHelpPage.getPageList().firstCheckPage(page)){
            return null;
        }

        l.add(new TextComponent(getSpacer("CastleRush Help &7(&b"+page+"&7/"
            +commandHelpPage.getPageList().getTotalPages()+"&7)")));
        l.addAll(commandHelpPage.get(page));

        if(page < commandHelpPage.getPageList().getTotalPages()){
            l.add(null);
            l.add(commandHelpPage.getNextPageComponent("/"+command, commandHelpPage.getPattern().getNextPagePattern()));
        }

        l.add(new TextComponent(getSpacer("CastleRush Help &7(&b"+page+"&7/"
                +commandHelpPage.getPageList().getTotalPages()+"&7)")));
        return l;
    }

    public static boolean materialExist(String name){
        return Material.getMaterial(name) != null;
    }


    public static class ItemStacks {

        public static final ItemStack MULTITOOL_STACK = new ItemBuilder(Material.DIAMOND_BARDING)
                .amount(1).unbreakable(true)
                .itemFlag(ItemFlag.HIDE_UNBREAKABLE, true)
                .name("&6Multitool - 3 in 1").lore("&7Sneak-Rightclick to change tool").build();

        public static final ItemStack MULTITOOL_STACK_SHOVEL = new ItemBuilder(Material.DIAMOND_SPADE)
                .amount(1).unbreakable(true)
                .itemFlag(ItemFlag.HIDE_UNBREAKABLE, true)
                .name("&6Multitool - 3 in 1").lore("&7Rightclick: Area pos1; Leftclick: Area pos2;").lore
                        ("&7Sneak+Leftclick: Single point")
                .build();

        public static final ItemStack MULTITOOL_STACK_PICKAXE = new ItemBuilder(Material.DIAMOND_PICKAXE)
                .amount(1).unbreakable(true)
                .itemFlag(ItemFlag.HIDE_UNBREAKABLE, true)
                .name("&6Multitool - 3 in 1").lore("&7Rightclick: Set pos1; Leftclick: Set pos2").build();

        public static final ItemStack MULTITOOL_STACK_HOE = new ItemBuilder(Material.DIAMOND_HOE)
                .amount(1).unbreakable(true)
                .itemFlag(ItemFlag.HIDE_UNBREAKABLE, true)
                .name("&6Multitool - 3 in 1").lore("&7Rightclick: Add spawnpoint; Leftclick: Remove one").build();
    }

}
