package de.superioz.cr.util;

import de.superioz.cr.main.CastleRush;
import de.superioz.library.bukkit.common.command.CommandHandler;
import de.superioz.library.bukkit.message.BukkitChat;
import de.superioz.library.bukkit.util.ChatUtil;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 * This class was created as a part of CastleRush (Spigot)
 *
 * @author Superioz
 */
public class PluginUtilities {

    public static PluginHelp commandHelpPage;
    public static final int COMMANDS_PER_PAGE = 12;

    static{
        commandHelpPage = new PluginHelp(COMMANDS_PER_PAGE, CommandHandler.getCommands());
    }

    /**
     * Get spacer for chat tables
     *
     * @param middle Text in the middle
     *
     * @return The spacer as string
     */
    public static String getSpacer(String middle){
        return ChatUtil.colored("&8===========[ &9" + middle + "&r &8]===========");
    }

    /**
     * Gets a list item
     *
     * @param item String in front of symbol
     *
     * @return The item as string
     */
    public static String getListItem(String item){
        return ChatUtil.colored("&8# " + item);
    }

    public static void getPluginInformationPage(Player player){
        List<String> str = Arrays.asList(
                getSpacer("CastleRush"),
                getListItem("&7Author: &a" + CastleRush.getInstance().getDescription().getAuthors().get(0)),
                getListItem("&7Commands: &e" + CommandHandler.getAllCommands().size()),
                getListItem("&7Version: &e" + CastleRush.getInstance().getDescription().getVersion()),
                getSpacer("CastleRush")
        );

        for(String s : str){ BukkitChat.send(s, player); }
    }

    /**
     * Check if material exist
     *
     * @param name Name of material
     *
     * @return Result as boolean
     */
    public static boolean materialExist(String name){
        return Material.getMaterial(name) != null;
    }

    /**
     * Copies given world
     *
     * @param name  name of target folder location
     * @param world The world
     */
    public static void copyWorld(String name, World world){
        File uid = null;
        try{
            FileUtils.copyDirectory(new File(Bukkit.getWorldContainer().getCanonicalPath() + "/" + world.getName()),
                    new File(Bukkit.getWorldContainer().getCanonicalPath() + "/" + name));
            uid = new File(Bukkit.getWorldContainer().getCanonicalPath() + "/" + name + "/uid.dat");
        }catch(Exception e){
            e.printStackTrace();
        }

        assert uid != null;
        if(uid.exists())
            uid.delete();
    }

}
