package de.superioz.cr.command;

import de.superioz.cr.common.WrappedGamePlayer;
import de.superioz.cr.common.arena.Arena;
import de.superioz.cr.common.arena.ArenaManager;
import de.superioz.cr.common.events.GameJoinEvent;
import de.superioz.cr.common.events.GameLeaveEvent;
import de.superioz.cr.common.events.GameStartEvent;
import de.superioz.cr.common.game.GameManager;
import de.superioz.cr.common.game.GameWall;
import de.superioz.cr.common.listener.GameListener;
import de.superioz.cr.main.CastleRush;
import de.superioz.library.java.util.classes.SimplePair;
import de.superioz.library.minecraft.server.command.annts.SubCommand;
import de.superioz.library.minecraft.server.command.cntxt.SubCommandContext;
import de.superioz.library.minecraft.server.util.geometry.GeometryUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
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
            CastleRush.getChatMessager().send(CastleRush.getProperties().get("youArentIngame"), player);
            return;
        }
        GameManager.Game game = GameManager.getGame(player);
        assert game != null;

        // Is the lobby full?
        if(!(game.enoughPlayers())){
            CastleRush.getChatMessager().send(CastleRush.getProperties().get("notEnoughPlayers"), player);
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
            CastleRush.getChatMessager().send(CastleRush.getProperties().get("youArentIngame"), player);
            return;
        }
        GameManager.Game game = GameManager.getGame(player);
        assert game != null;

        // Is the game really finished?
        if(game.getArena().getGameState() != GameManager.State.WAITING){
            CastleRush.getChatMessager().send(CastleRush.getProperties().get("gameIsntFinish"), player);
            return;
        }

        // teleport all players back
        for(WrappedGamePlayer pl : game.getArena().getPlayers()){
            pl.teleport(pl.getJoinLocation());
        }
        game.leaveAll();

        // set gamestate
        game.getArena().setGameState(GameManager.State.LOBBY);
        GameManager.removeGameFromQueue(game);
    }

    @SubCommand(name = "timeleft", aliases = "tl", permission = "castlerush.timeleft"
            , desc = "Shows the counter from current timer")
    public void timeLeft(SubCommandContext commandContext){
        Player player = (Player) commandContext.getSender();

        if(!GameManager.isIngame(player)){
            CastleRush.getChatMessager().send(CastleRush.getProperties().get("youArentIngame"), player);
            return;
        }
        GameManager.Game game = GameManager.getGame(player);
        assert game != null;

        if((game.getArena().getGameState() != GameManager.State.INGAME)
                || GameListener.countdown == null){
            CastleRush.getChatMessager().send(CastleRush.getProperties().get("noTimerAtTheMoment"), player);
            return;
        }

        int counter = GameListener.countdown.getCounter();
        int seconds = counter % 60;
        int minutes = counter / 60;
        int hours = minutes / 60;

        CastleRush.getChatMessager().send(CastleRush.getProperties().get("timeLeft")
                .replace("%hours", hours+"").replace("%minutes", minutes+"").replace("%seconds", seconds+""),
                    player);
    }

    @SubCommand(name = "timegone", aliases = "tg", permission = "castlerush.timegone"
            , desc = "Shows how much time gone from jump-run begin")
    public void timeGone(SubCommandContext context){
        Player player = (Player) context.getSender();

        if(!GameManager.isIngame(player)){
            CastleRush.getChatMessager().send(CastleRush.getProperties().get("youArentIngame"), player);
            return;
        }
        GameManager.Game game = GameManager.getGame(player);
        assert game != null;

        long timeStamp = System.currentTimeMillis();
        long oldTimeStamp = game.getTimeStamp();

        if(oldTimeStamp != 0){
            int diff = (int) ((timeStamp-oldTimeStamp)/1000);

            int seconds = diff % 60;
            int minutes = diff / 60;
            int hours = minutes / 60;

            CastleRush.getChatMessager().send(CastleRush.getProperties().get("timeGone")
                    .replace("%hours", hours+"")
                    .replace("%minutes", minutes+"")
                    .replace("%seconds", seconds+""), player);
        }
    }

    @SubCommand(name = "join", aliases = "j", permission = "castlerush.join"
            , desc = "Joins given arena", min = 1)
    public void join(SubCommandContext context){
        Player player = (Player) context.getSender();

        if(GameManager.isIngame(player)){
            CastleRush.getChatMessager().send(CastleRush.getProperties().get("youAreAlreadyIngame"), player);
            return;
        }

        String arenaName = ArenaManager.getName(context, 0);

        if(!ArenaManager.checkArenaName(arenaName)
                || ArenaManager.EditorCache.contains(arenaName)){
            CastleRush.getChatMessager().send(CastleRush.getProperties().get("nameNotValid"), player);
            return;
        }

        Arena arena = ArenaManager.get(arenaName);
        if(!GameManager.containsGameInQueue(arena)){
            CastleRush.getChatMessager().send(CastleRush.getProperties().get("cannotJoinGameViaCommand"), player);
            return;
        }
        GameManager.Game game = GameManager.getGame(arena);
        assert game != null;

        if(game.getArena().getGameState() != GameManager.State.LOBBY){
            CastleRush.getChatMessager().send(CastleRush.getProperties().get("cannotJoinArena"), player);
            return;
        }

        // Call event for further things
        CastleRush.getPluginManager().callEvent(new GameJoinEvent(game, player, player.getLocation()));
    }

    @SubCommand(name = "leave", aliases = "l", permission = "castlerush.leave"
            , desc = "Leaves given arena")
    public void leave(SubCommandContext context){
        Player player = (Player) context.getSender();

        if(!GameManager.isIngame(player)){
            CastleRush.getChatMessager().send(CastleRush.getProperties().get("youArentIngame"), player);
            return;
        }

        GameManager.Game game = GameManager.getGame(player); assert game != null;

        CastleRush.getPluginManager().callEvent(new GameLeaveEvent(game, player));
        CastleRush.getChatMessager().send(CastleRush.getProperties().get("leftTheGame"), player);
    }

    @SubCommand(name = "forcetimer", aliases = {"forcet", "ft"}, permission = "castlerush.forcetimer"
            , desc = "Forces the timer in arena")
    public void forceTimer(SubCommandContext context){
        Player player = (Player) context.getSender();

        if(!GameManager.isIngame(player)){
            CastleRush.getChatMessager().send(CastleRush.getProperties().get("youArentIngame"), player);
            return;
        }

        int timer = GameListener.countdown.getCounter();

        if(timer >= 15){
            GameListener.countdown.setCounter(15);
            CastleRush.getChatMessager().send(CastleRush.getProperties().get("shortenedTime")
                    .replace("%sec", 15+""), player);
        }
        else{
            CastleRush.getChatMessager().send(CastleRush.getProperties().get("noNeedForShorten"), player);
        }
    }

    @SubCommand(name = "isingame", aliases = "ii", permission = "castlerush.isingame"
            , desc = "Joins given arena", min = 1, usage = "[player]")
    public void isIngame(SubCommandContext context){
        Player player = (Player) context.getSender();

        String name = context.argument(0);
        if(Bukkit.getPlayer(name) == null){
            CastleRush.getChatMessager().send(CastleRush.getProperties().get("playerIsntOnline"), player);
            return;
        }

        Player target = Bukkit.getPlayer(name);
        boolean b = GameManager.isIngame(target);

        GameManager.Game game = GameManager.getGame(target);

        assert game != null;
        CastleRush.getChatMessager().send(CastleRush.getProperties().get("isIngameMessage")
                .replace("%ingameState", (b ?
                                CastleRush.getProperties().get("isIngameState")
                                        .replace("%arena", game.getArena().getArena().getName())
                        : CastleRush.getProperties().get("isNotIngameState")))
                .replace("%player", target.getDisplayName()), player);
    }

    @SubCommand(name = "setwalls", aliases = {"setw", "sw"}, permission = "castlerush.setwalls"
            , desc = "Sets all walls ingame")
    public void setwalls(SubCommandContext context){
        Player player = (Player) context.getSender();

        if(!GameManager.isIngame(player)){
            CastleRush.getChatMessager().send(CastleRush.getProperties().get("youArentIngame"), player);
            return;
        }
        GameManager.Game game = GameManager.getGame(player);
        assert game != null;

        // Get wall
        for(GameWall wall : game.getArena().getArena().getGameWalls()){
            SimplePair<Location, Location> boundaries = wall.getBoundaries();

            if(context.argumentsLength() == 0){
                // Toggles the walls
                for(Location l : GeometryUtils.cuboid(boundaries.getType1(), boundaries.getType2())){
                    l.getBlock().setType(Material.AIR);
                }
            }
            else if(context.argumentsLength() >= 1){
                String material = context.argument(0).toUpperCase();
                game.setWalls(material);
            }
        }
    }

}
