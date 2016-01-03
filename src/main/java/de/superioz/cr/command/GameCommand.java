package de.superioz.cr.command;

import de.superioz.cr.common.ChatManager;
import de.superioz.cr.common.WrappedGamePlayer;
import de.superioz.cr.common.arena.Arena;
import de.superioz.cr.common.arena.ArenaManager;
import de.superioz.cr.common.cache.EditorCache;
import de.superioz.cr.common.event.GameJoinEvent;
import de.superioz.cr.common.event.GameLeaveEvent;
import de.superioz.cr.common.event.GamePhaseEvent;
import de.superioz.cr.common.game.*;
import de.superioz.cr.common.game.team.Team;
import de.superioz.cr.common.lang.LanguageManager;
import de.superioz.cr.common.timer.GameCountdown;
import de.superioz.cr.util.TimeType;
import de.superioz.library.java.util.list.ListUtil;
import de.superioz.library.main.SuperLibrary;
import de.superioz.library.minecraft.server.common.command.SubCommand;
import de.superioz.library.minecraft.server.common.command.context.CommandContext;
import de.superioz.library.minecraft.server.event.WrappedInventoryClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * This class was created as a part of CastleRush (Spigot)
 *
 * @author Superioz
 */
public class GameCommand {

    @SubCommand(label = "startgame", aliases = "start", permission = "castlerush.startgame"
        , desc = "Starts the current game")
    public void startGame(CommandContext commandContext){
        Player player = (Player) commandContext.getSender();

        if(!GameManager.isIngame(player.getUniqueId())){
            ChatManager.info().write(LanguageManager.get("youArentIngame"), player);
            return;
        }
        Game game = GameManager.getGame(player);
        assert game != null;

        // Check if the player is the game master
        if(!game.isGamemaster(player)){
            ChatManager.info().write(LanguageManager.get("youArentGamemaster"), player);
            return;
        }

        // Check if the game can be started
        if(game.getArena().getGameState() != GameState.LOBBY){
            ChatManager.info().write(LanguageManager.get("gameAlreadyStarted"), player);
            return;
        }

        // Check game mode (automatic?)
        if(game.getType() == GameType.PUBLIC){
            game.getGameCountdown().getLobbyToBuild().setCounter(GameCountdown.LAST_COUNTDOWN);
            return;
        }

        // Is the lobby full?
        if(!(game.enoughPlayers())){
            ChatManager.info().write(LanguageManager.get("notEnoughPlayers"), player);
            return;
        }

        // Call event, that the game start
        SuperLibrary.callEvent(new GamePhaseEvent(game, GamePhase.BUILD));
    }

    @SubCommand(label = "finishgame", aliases = "finish", permission = "castlerush.finishgame"
        , desc = "Finished the current game")
    public void finishGame(CommandContext commandContext){
        Player player = (Player) commandContext.getSender();

        if(!GameManager.isIngame(player.getUniqueId())){
            ChatManager.info().write(LanguageManager.get("youArentIngame"), player);
            return;
        }
        Game game = GameManager.getGame(player);
        assert game != null;

        // Check game mode (automatic?)
        if(game.getType() == GameType.PUBLIC){
            ChatManager.info().write(LanguageManager.get("cannotUseCommandInPublicGame"), player);
            return;
        }

        // Check if the player is the game master
        if(!game.isGamemaster(player)){
            ChatManager.info().write(LanguageManager.get("youArentGamemaster"), player);
            return;
        }

        // Is the game really finished?
        if(game.getArena().getGameState() != GameState.WAITING){
            ChatManager.info().write(LanguageManager.get("gameIsntFinish"), player);
            return;
        }

        // teleport all players back
        SuperLibrary.callEvent(new GamePhaseEvent(game, GamePhase.FINISH));
    }

    @SubCommand(label = "timeleft", aliases = "tl", permission = "castlerush.timeleft"
            , desc = "Shows the counter from current timer")
    public void timeLeft(CommandContext commandContext){
        Player player = (Player) commandContext.getSender();

        if(!GameManager.isIngame(player.getUniqueId())){
            ChatManager.info().write(LanguageManager.get("youArentIngame"), player);
            return;
        }
        Game game = GameManager.getGame(player);
        assert game != null;

        if(game.getArena().getGamePhase() != GamePhase.BUILD){
            ChatManager.info().write(LanguageManager.get("noTimerAtTheMoment"), player);
            return;
        }

        ChatManager.info().write(LanguageManager.get("timeLeft")
                .replace("%hours", game.getTime(TimeType.HOURS))
                .replace("%minutes", game.getTime(TimeType.MINUTES))
                .replace("%seconds", game.getTime(TimeType.SECONDS)), player);
    }

    @SubCommand(label = "timegone", aliases = "tg", permission = "castlerush.timegone"
            , desc = "Shows how much time gone from jump-run begin")
    public void timeGone(CommandContext context){
        Player player = (Player) context.getSender();

        if(!GameManager.isIngame(player.getUniqueId())){
            ChatManager.info().write(LanguageManager.get("youArentIngame"), player);
            return;
        }
        Game game = GameManager.getGame(player);
        assert game != null;

        long timeStamp = System.currentTimeMillis();
        long oldTimeStamp = game.getTimeStamp();

        if(oldTimeStamp != 0
                && game.getArena().getGamePhase() == GamePhase.CAPTURE){
            ChatManager.info().write(LanguageManager.get("timeGone")
                    .replace("%hours", game.getTime(TimeType.HOURS))
                    .replace("%minutes", game.getTime(TimeType.MINUTES))
                    .replace("%seconds", game.getTime(TimeType.SECONDS)), player);
        }
        else {
            ChatManager.info().write(LanguageManager.get("noTimeGoneYet"), player);
        }
    }

    @SubCommand(label = "join", aliases = "j", permission = "castlerush.join"
            , desc = "Joins given arena", min = 1, usage = "[arena]")
    public void join(CommandContext context){
        Player player = (Player) context.getSender();

        if(GameManager.isIngame(player.getUniqueId())){
            ChatManager.info().write(LanguageManager.get("youAreAlreadyIngame"), player);
            return;
        }

        String arenaName = ArenaManager.getName(context, 1);

        if(!ArenaManager.checkArenaName(arenaName)
                || EditorCache.contains(arenaName)){
            ChatManager.info().write(LanguageManager.get("nameNotValid"), player);
            return;
        }

        Arena arena = ArenaManager.get(arenaName);
        if(arena == null){
            ChatManager.info().write(LanguageManager.get("arenaDoesntExist"), player);
            return;
        }

        if(!GameManager.containsGameInQueue(arena)){
            ChatManager.info().write(LanguageManager.get("cannotJoinGameViaCommand"), player);
            return;
        }

        // Call event for further things
        SuperLibrary.callEvent(new GameJoinEvent(arena, player, player.getLocation()));
    }

    @SubCommand(label = "leave", aliases = "l", permission = "castlerush.leave"
            , desc = "Leaves given arena")
    public void leave(CommandContext context){
        Player player = (Player) context.getSender();

        if(!GameManager.isIngame(player.getUniqueId())){
            ChatManager.info().write(LanguageManager.get("youArentIngame"), player);
            return;
        }

        Game game = GameManager.getGame(player); assert game != null;

        SuperLibrary.callEvent(new GameLeaveEvent(game, GameManager.getWrappedGamePlayer(player),
                GameLeaveEvent.Type.COMMAND_LEAVE));
        ChatManager.info().write(LanguageManager.get("leftTheGame"), player);
    }

    @SubCommand(label = "forcetimer", aliases = {"forcet", "ft"}, permission = "castlerush.forcetimer"
            , desc = "Forces the timer in arena")
    public void forceTimer(CommandContext context){
        Player player = (Player) context.getSender();

        if(!GameManager.isIngame(player.getUniqueId())){
            ChatManager.info().write(LanguageManager.get("youArentIngame"), player);
            return;
        }
        Game game = GameManager.getGame(player);
        assert game != null;

        int timer = game.getBuildCountdown().getRepeater().getCounter();

        if(timer >= GameCountdown.LAST_COUNTDOWN){
            game.getBuildCountdown().getRepeater().setCounter(GameCountdown.LAST_COUNTDOWN);
            ChatManager.info().write(LanguageManager.get("shortenedTime")
                    .replace("%sec", GameCountdown.LAST_COUNTDOWN+""), player);
        }
        else{
            ChatManager.info().write(LanguageManager.get("noNeedForShorten"), player);
        }
    }

    @SubCommand(label = "isingame", aliases = "ii", permission = "castlerush.isingame"
            , desc = "Joins given arena", min = 1, usage = "[player]")
    public void isIngame(CommandContext context){
        Player player = (Player) context.getSender();

        String name = context.getArgument(0);
        if(Bukkit.getPlayer(name) == null){
            ChatManager.info().write(LanguageManager.get("playerIsntOnline"), player);
            return;
        }

        Player target = Bukkit.getPlayer(name);
        boolean b = GameManager.isIngame(target.getUniqueId());

        Game game = GameManager.getGame(target);

        assert game != null;
        ChatManager.info().write(LanguageManager.get("isIngameMessage")
                .replace("%ingameState", (b ?
                                LanguageManager.get("isIngameState")
                                        .replace("%arena", game.getArena().getArena().getName())
                        : LanguageManager.get("isNotIngameState")))
                .replace("%player", target.getDisplayName()), player);
    }

    @SubCommand(label = "setwalls", aliases = {"setw", "sw"}, permission = "castlerush.setwalls"
            , desc = "Sets all walls ingame")
    public void setwalls(CommandContext context){
        Player player = (Player) context.getSender();

        if(!GameManager.isIngame(player.getUniqueId())){
            ChatManager.info().write(LanguageManager.get("youArentIngame"), player);
            return;
        }
        Game game = GameManager.getGame(player);
        assert game != null;

        // Check if command can be used in game type
        if(game.getType() == GameType.PUBLIC){
            ChatManager.info().write(LanguageManager.get("cannotUseCommandInPublicGame"), player);
            return;
        }

        // Check if the player is the game master
        if(!game.isGamemaster(player)){
            ChatManager.info().write(LanguageManager.get("youArentGamemaster"), player);
            return;
        }

        // Get wall
        for(GameWall wall : game.getArena().getArena().getGameWalls()){
            if(context.getArgumentsLength() == 0){
                // Toggles the walls
                game.getArena().resetWalls();
            }
            else if(context.getArgumentsLength() >= 1){
                String material = context.getArgument(1).toUpperCase();
                game.getArena().setWalls(Material.getMaterial(material));
            }
        }
    }

    @SubCommand(label = "teammates", aliases = {"teamm", "tm", "mates"}, permission = "castlerush.teammates"
            , desc = "Shows your teammates")
    public void teammates(CommandContext context){
        Player player = (Player) context.getSender();

        if(!GameManager.isIngame(player.getUniqueId())){
            ChatManager.info().write(LanguageManager.get("youArentIngame"), player);
            return;
        }
        Game game = GameManager.getGame(player);
        assert game != null;
        WrappedGamePlayer gp = GameManager.getWrappedGamePlayer(player);

        String s = ListUtil.insert(gp.getTeamMatesNames(), ", ");
        ChatManager.info().write(LanguageManager.get("teamMates")
                .replace("%pl", s.isEmpty() ? LanguageManager.get("youDontHaveTeammates"): s)
                .replace("%team", gp.getTeam() == null ? "NO TEAM" : gp.getTeam().getColoredName(gp.getGame())), player);
    }

    @SubCommand(label = "teams", permission = "castlerush.teams"
            , desc = "Shows the teams")
    public void teams(CommandContext context){
        Player player = (Player) context.getSender();

        if(!GameManager.isIngame(player.getUniqueId())){
            ChatManager.info().write(LanguageManager.get("youArentIngame"), player);
            return;
        }
        Game game = GameManager.getGame(player);
        assert game != null;
        WrappedGamePlayer gp = GameManager.getWrappedGamePlayer(player);
        ChatManager.info().write(LanguageManager.get("teamOverviewHeader"), player);

        for(Team t : game.getTeamManager().getTeams()){
            List<String> teamPlayer = new ArrayList<>();
            t.getTeamPlayer().forEach(wrappedGamePlayer
                    -> teamPlayer.add(wrappedGamePlayer.getPlayer().getDisplayName()));

            ChatManager.info().write(LanguageManager.get("teamOverviewItem")
                    .replace("%teamName", t.getColoredName(gp.getGame()))
                    .replace("%player", ListUtil.insert(teamPlayer, ", ")), player);
        }
    }

    @SubCommand(label = "games", permission = "castlerush.games"
            , desc = "Shows all running games")
    public void games(CommandContext context){
        Player player = (Player) context.getSender();

        if(GameManager.getRunningGames().size() == 0){
            ChatManager.info().write("&cNo game running at the moment!", player);
            return;
        }

        player.openInventory(GameManager.getGameOverview("Running games", WrappedInventoryClickEvent::cancelEvent).build());
    }

    @SubCommand(label = "forcestop", aliases = {"forcest", "fs"}, permission = "castlerush.forcestop"
            , desc = "Stops your current arena")
    public void forcestop(CommandContext context){
        Player player = (Player) context.getSender();

        if(!GameManager.isIngame(player.getUniqueId())){
            ChatManager.info().write(LanguageManager.get("youArentIngame"), player);
            return;
        }
        Game game = GameManager.getGame(player);
        assert game != null;

        ChatManager.info().write(LanguageManager.get("waitingForStopGame"), player);
        if(game.getArena().getGameState() == GameState.LOBBY){
            ChatManager.info().write(LanguageManager.get("couldntStopGame"), player);
            return;
        }

        // teleport all players back
        SuperLibrary.callEvent(new GamePhaseEvent(game, GamePhase.FINISH));
    }

    @SubCommand(label = "gamemode", aliases = {"gm"}, permission = "castlerush.gamemode"
            , desc = "Gives you gamemode")
    public void gamemode(CommandContext context){
        Player player = (Player) context.getSender();

        if(!GameManager.isIngame(player.getUniqueId())){
            ChatManager.info().write(LanguageManager.get("youArentIngame"), player);
            return;
        }
        WrappedGamePlayer gamePlayer = GameManager.getWrappedGamePlayer(player);

        if(!gamePlayer.isOnPlot()){
            ChatManager.info().write(LanguageManager.get("youArentOnYourPlot"), player);
            return;
        }
        gamePlayer.setGameMode(GameMode.CREATIVE);
    }


}
