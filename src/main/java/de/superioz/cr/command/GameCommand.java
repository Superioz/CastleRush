package de.superioz.cr.command;

import de.superioz.cr.common.WrappedGamePlayer;
import de.superioz.cr.common.arena.Arena;
import de.superioz.cr.common.arena.ArenaManager;
import de.superioz.cr.common.events.GameJoinEvent;
import de.superioz.cr.common.events.GameStartEvent;
import de.superioz.cr.common.game.GameManager;
import de.superioz.cr.common.game.PlayableArena;
import de.superioz.cr.common.listener.GameListener;
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

        // teleport all players back
        for(WrappedGamePlayer p : game.getArena().getPlayers()){
            p.getPlayer().teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
            game.leave(p.getPlayer());
        }

        // set gamestate
        game.getArena().setGameState(GameManager.State.LOBBY);
    }

    @SubCommand(name = "timeleft", aliases = "tl", permission = "castlerush.timeleft"
            , desc = "Shows the counter from current timer")
    public void timeLeft(SubCommandContext commandContext){
        Player player = (Player) commandContext.getSender();

        if(!GameManager.isIngame(player)){
            CastleRush.getChatMessager().send("&cYou are not ingame!", player);
            return;
        }
        GameManager.Game game = GameManager.getGame(player);
        assert game != null;

        if((game.getArena().getGameState() != GameManager.State.INGAME)
                || GameListener.countdown == null){
            CastleRush.getChatMessager().send("&cThere is no timer at the moment!", player);
            return;
        }

        int counter = GameListener.countdown.getCounter();
        int seconds = counter % 60;
        int minutes = counter / 60;
        int hours = minutes / 60;

        CastleRush.getChatMessager().send("&7Time left: &a"
                + hours + "&7h &a" + minutes + "&7m &a" + seconds + "&7s", player);
    }

    @SubCommand(name = "join", aliases = "j", permission = "castlerush.join"
            , desc = "Joins given arena", min = 1)
    public void join(SubCommandContext context){
        Player player = (Player) context.getSender();

        if(GameManager.isIngame(player))
            return;

        String arenaName = "";
        for(int i = 0; i < context.argumentsLength(); i++){
            String add = "";
            if(i == context.argumentsLength())
                add = " ";

            arenaName += context.argument(i) + add;
        }

        if(!ArenaManager.checkArenaName(arenaName)
                || ArenaManager.EditorCache.contains(arenaName)){
            CastleRush.getChatMessager().send("&cThat name isn't valid!", player);
            return;
        }

        Arena arena = ArenaManager.get(arenaName);

        if(!GameManager.containsGameInQueue(arena)){
            GameManager.addGameInQueue(new GameManager.Game(new PlayableArena(arena, GameManager.State.LOBBY)));
        }
        GameManager.Game game = GameManager.getGame(arena);
        assert game != null;

        if(game.getArena().getGameState() != GameManager.State.LOBBY){
            CastleRush.getChatMessager().send("&cYou cannot join this arena!", player);
            return;
        }

        // Call event for further things
        CastleRush.getPluginManager().callEvent(new GameJoinEvent(game, player));
    }

}
