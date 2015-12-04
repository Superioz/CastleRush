package de.superioz.cr.command;

import de.superioz.cr.util.CommandHelpPage;
import de.superioz.cr.main.CastleRush;
import de.superioz.cr.util.Utilities;
import de.superioz.library.java.util.SimpleStringUtils;
import de.superioz.library.minecraft.server.common.command.*;
import de.superioz.library.minecraft.server.common.command.context.CommandContext;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

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
        Utilities.getPluginInformationPage((Player) commandContext.getSender());
    }

    @SubCommand(label = "help", aliases = "?", desc = "Shows the help page",
            permission = "castlerush.help", usage = "<page>")
    public void help(CommandContext commandContext){
        int page = 1;
        Player player = (Player) commandContext.getSender();

        if(commandContext.getArgumentsLength() >= 1){
            String arg = commandContext.getArgument(1);

            if(SimpleStringUtils.isInteger(arg))
                page = Integer.parseInt(arg);
        }

        CommandHelpPage commandHelpPage = new CommandHelpPage(12, CommandHandler.getAllCommands());

        List<TextComponent> textComponents = commandHelpPage.getPage(page);
        if(textComponents == null){
            CastleRush.getChatMessager().write(CastleRush.getProperties().get("helpCommandPageDoesntExist"), player);
            return;
        }

        for(TextComponent tc : textComponents){
            if(tc == null){
                CastleRush.getChatMessager().write(Utilities.getListItem(""), player);
                continue;
            }

            player.spigot().sendMessage(tc);
        }
    }

}
