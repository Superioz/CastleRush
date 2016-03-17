package de.superioz.cr.command;

import de.superioz.cr.common.ChatManager;
import de.superioz.cr.common.lang.LanguageManager;
import de.superioz.cr.util.PluginHelp;
import de.superioz.cr.util.PluginUtilities;
import de.superioz.library.bukkit.common.command.*;
import de.superioz.library.bukkit.common.command.context.CommandContext;
import de.superioz.library.java.util.SimpleStringUtils;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * This class was created as a part of CastleRush (Spigot)
 *
 * @author Superioz
 */

@Command(label = "castlerush",
    desc = "Main command for CastleRush",
    aliases = "cr",
        permission = "castlerush.main",
    commandTarget = AllowedCommandSender.PLAYER)
public class MainCommand implements CommandCase {

    @Override
    public void execute(CommandContext commandContext){
        PluginUtilities.getPluginInformationPage((Player) commandContext.getSender());
    }

    @Command(label = "help", aliases = "?", desc = "Shows the help page",
            permission = "castlerush.help", usage = "<page>")
    public void help(CommandContext commandContext){
        int page = 1;
        Player player = (Player) commandContext.getSender();

        if(commandContext.getArgumentsLength() >= 1){
            String arg = commandContext.getArgument(1);

            if(SimpleStringUtils.isInteger(arg))
                page = Integer.parseInt(arg);
        }

	    // Get commands and plugin help
        List<CommandWrapper> commands = CommandHandler.getAllCommands();
        PluginHelp commandHelpPage = new PluginHelp(12, commands);

        List<TextComponent> textComponents = commandHelpPage.getPage(page);
        if(textComponents == null){
            ChatManager.info().write(LanguageManager.get("helpCommandPageDoesntExist"), player);
            return;
        }

        for(TextComponent tc : textComponents){
            if(tc == null){
                ChatManager.info().write(PluginUtilities.getListItem(""), player);
                continue;
            }

            player.spigot().sendMessage(tc);
        }
    }

}
