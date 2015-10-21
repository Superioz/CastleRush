package de.superioz.cr.command;

import de.superioz.cr.common.WrappedGamePlayer;
import de.superioz.cr.common.events.GameStartEvent;
import de.superioz.cr.common.game.GameManager;
import de.superioz.cr.main.CastleRush;
import de.superioz.library.minecraft.server.command.annts.SubCommand;
import de.superioz.library.minecraft.server.command.cntxt.SubCommandContext;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * This class was created as a part of CastleRush (Spigot)
 *
 * @author Superioz
 */
public class GameCommand {

    @SubCommand(name = "startgame", aliases = "start", permission = "castlerush.startgame"
        , desc = "Starts the current game")
    public void startGame(SubCommandContext commandContext){
        Player player = (Player) commandContext.getSender();

        if(!GameManager.isIngame(player)){
            CastleRush.getChatMessager().send("&cYou are not ingame!", player);
            return;
        }
        GameManager.Game game = GameManager.getGame(player);
        assert game != null;

        // Is the lobby full?
        if(!(game.getArena().getGameState() == GameManager.State.FULL)){
            CastleRush.getChatMessager().send("&cThe lobby is not full!", player);
            return;
        }

        // Call event, that the game start
        CastleRush.getPluginManager().callEvent(new GameStartEvent(game));
    }

    @SubCommand(name = "finishgame", aliases = "finish", permission = "castlerush.finishgame"
        , desc = "Finished the current game")
    public void finishGame(SubCommandContext commandContext){
        Player player = (Player) commandContext.getSender();

        if(!GameManager.isIngame(player)){
            CastleRush.getChatMessager().send("&cYou are not ingame!", player);
            return;
        }
        GameManager.Game game = GameManager.getGame(player);

        // Is the game really finished?
        assert game != null;
        if(!(game.getArena().getGameState() == GameManager.State.WAITING)){
            return;
        }

        // teleport all playyers back
        for(WrappedGamePlayer p : game.getArena().getPlayers()){
            p.getPlayer().teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
            game.leave(p.getPlayer());
        }


        // set gamestate
        game.getArena().setGameState(GameManager.State.LOBBY);
    }

}
