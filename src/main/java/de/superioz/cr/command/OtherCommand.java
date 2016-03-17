package de.superioz.cr.command;

import de.superioz.cr.common.ChatManager;
import de.superioz.cr.common.lang.LanguageManager;
import de.superioz.cr.common.arena.Arena;
import de.superioz.cr.common.settings.PluginSettings;
import de.superioz.cr.util.PluginUtilities;
import de.superioz.library.bukkit.common.command.Command;
import de.superioz.library.bukkit.common.command.context.CommandContext;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;

/**
 * This class was created as a part of CastleRush
 *
 * @author Superioz
 */
public class OtherCommand {

    @Command(label = "reload", aliases = "rl", desc = "Reloads the config",
            permission = "castlerush.reload")
    public void help(CommandContext commandContext){
        Player player = (Player) commandContext.getSender();

        ChatManager.info().write("&eReload config ..", player);

        // Reload settings
        PluginSettings.reload();

        ChatManager.info().write("&eReloaded.", player);
    }

    @Command(label = "template", aliases = {"templ"}, desc = "Creates a copy of given world",
            permission = "castlerush.template", min = 1, usage = "[worldName]")
    public void template(CommandContext commandContext) throws IOException{
        Player player = (Player) commandContext.getSender();

        String name = commandContext.getArgument(1);
        ChatManager.info().write(LanguageManager.get("createTemplate").replace("%name", name), player);
        if(Bukkit.getWorld(name) == null ||
                new File(Bukkit.getWorldContainer().getCanonicalPath() + "/"
                        + name + Arena.TEMPLATE_ATTACHMENT).exists()){
            ChatManager.info().write(LanguageManager.get("couldntCreateTemplate"), player);
            return;
        }

        PluginUtilities.copyWorld(name + Arena.TEMPLATE_ATTACHMENT, Bukkit.getWorld(name));
        ChatManager.info().write(LanguageManager.get("templateCreated"), player);
    }


}
