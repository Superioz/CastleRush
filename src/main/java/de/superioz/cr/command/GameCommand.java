package de.superioz.cr.command;

import de.superioz.cr.common.game.GameManager;
import de.superioz.cr.main.CastleRush;
import de.superioz.library.minecraft.server.command.annts.SubCommand;
import de.superioz.library.minecraft.server.command.cntxt.SubCommandContext;
import org.bukkit.entity.Player;

/**
 * This class was created as a part of CastleRush (Spigot)
 *
 * @author Superioz
 */
public class GameCommand {

    @SubCommand(name = "startgame", aliases = "start", permission = "castlerush.startgame")
    public void startGame(SubCommandContext commandContext){
        Player player = (Player) commandContext.getSender();

        if(!GameManager.isIngame(player)){
            CastleRush.getChatMessager().send("&cYou are not ingame!", player);
            return;
        }
        GameManager.Game game = GameManager.getGame(player);

    }

}
