package de.superioz.cr.command;

import de.superioz.cr.main.CastleRush;
import de.superioz.cr.util.Utilities;
import de.superioz.library.java.util.SimpleStringUtils;
import de.superioz.library.minecraft.server.command.annts.Command;
import de.superioz.library.minecraft.server.command.annts.SubCommand;
import de.superioz.library.minecraft.server.command.cntxt.CommandContext;
import de.superioz.library.minecraft.server.command.cntxt.SubCommandContext;
import de.superioz.library.minecraft.server.command.enms.AllowedCommandSender;
import de.superioz.library.minecraft.server.command.wrpper.CommandWrapper;
import de.superioz.library.minecraft.server.util.chat.BukkitChat;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * This class was created as a part of CastleRush (Spigot)
 *
 * @author Superioz
 */

@Command(name = "castlerush",
    desc = "Main command for CastleRush",
    aliases = "cr",
    commandTarget = AllowedCommandSender.PLAYER)
public class MainCommand implements CommandWrapper {

    @Override
    public void handle(CommandContext commandContext){
        Utilities.getPluginInformationPage((Player) commandContext.getSender());
    }

    @SubCommand(name = "help", aliases = "?", desc = "Shows the help page", permission = "castlerush.help", usage =
            "<page>")
    public void help(SubCommandContext commandContext){
        int page = 1;
        Player player = (Player) commandContext.getSender();

        if(commandContext.argumentsLength() >= 1){
            String arg = commandContext.argument(0);

            if(SimpleStringUtils.isInteger(arg))
                page = Integer.parseInt(arg);
        }

        String nextPageCommand = commandContext.getParent().getLabel() + " " + commandContext.getLabel()
                + " " + (page+1);
        Utilities.initCommandHelp(nextPageCommand);

        List<TextComponent> textComponents = Utilities.getCommandHelp(nextPageCommand, page);
        if(textComponents == null){
            CastleRush.getChatMessager().send("&cThis page doesn't exist!", player);
            return;
        }

        for(TextComponent tc : textComponents){
            if(tc == null){
                BukkitChat.send(Utilities.getListItem(""), player);
                continue;
            }

            player.spigot().sendMessage(tc);
        }
    }

}
