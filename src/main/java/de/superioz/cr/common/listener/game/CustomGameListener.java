package de.superioz.cr.common.listener.game;

import de.superioz.cr.common.ChatManager;
import de.superioz.cr.common.EventManager;
import de.superioz.cr.common.lang.LanguageManager;
import de.superioz.cr.common.WrappedGamePlayer;
import de.superioz.cr.common.arena.Arena;
import de.superioz.cr.common.event.*;
import de.superioz.cr.common.game.*;
import de.superioz.cr.common.settings.PluginSettings;
import de.superioz.cr.main.CastleRush;
import de.superioz.cr.util.PluginItems;
import de.superioz.library.main.SuperLibrary;
import de.superioz.library.minecraft.server.common.runnable.SuperDelayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

/**
 * This class was created as a part of CastleRush (Spigot)
 *
 * @author Superioz
 */
public class CustomGameListener implements Listener {

    @EventHandler
    public void onGameJoin(GameJoinEvent event){
        Player player = event.getPlayer();
        Arena arena = event.getArena();

        // Check if player can join this ARENA
        if(!arena.checkJoinable(player).isEmpty()){
            ChatManager.info().write(LanguageManager.get("youCannotJoinThisArenaReason")
                    .replace("%reason", arena.checkJoinable(player)), player.getPlayer());
            return;
        }

        // Check if arena is in game queue
        if(!GameManager.containsGameInQueue(arena)){
            ChatManager.info().write(LanguageManager.get("errorWhileJoining"), player);
            return;
        }
        Game game = GameManager.getGame(arena);
        assert game != null;

        // If the game is not in the lobby
        if(game.getArena().getGameState() != GameState.LOBBY){
            ChatManager.info().write(LanguageManager.get("youCannotJoinThisArena"), player);
            return;
        }

        // Check if player can join this GAME
        if(!game.checkJoinable(player).isEmpty()){
            ChatManager.info().write(LanguageManager.get("youCannotJoinThisArenaReason")
                    .replace("%reason", game.checkJoinable(player)), player.getPlayer());
            return;
        }

        // Join the game
        game.join(player.getPlayer(), event.getLoc());
        List<WrappedGamePlayer> currentPlayers = game.getArena().getPlayers();
        int currentPlayersSize = currentPlayers.size();

        // Teleport to spawnpoint
        player.teleport(game.getArena().getArena().getSpawnPoint());
        game.getArena().getPlayer(player).clear();

        // Send message
        game.broadcast(LanguageManager.get("playerJoinedTheGame")
                .replace("%player", player.getPlayer().getDisplayName()).replace("%current", currentPlayersSize + ""));

        // Check players count
        if(currentPlayersSize < game.getMinSize()){
            // There must be more players
            game.broadcast(LanguageManager.get("playersLeftToStart")
                    .replace("%size", (game.getMinSize() - currentPlayersSize) + ""));
        }
        else if(currentPlayersSize == game.getMinSize()){
            if(game.getType() == GameType.PRIVATE){
                game.broadcast(LanguageManager.get("enoughPlayersToStart"));
            }
            else{
                game.getGameCountdown().runLobbyTimer();
            }
        }
        if(currentPlayersSize == 1){
            game.start();
        }

        // Give team choose tool
        if(PluginSettings.TEAM_MODE){
            new BukkitRunnable() {
                @Override
                public void run(){
                    player.getInventory().addItem(PluginItems.TEAM_CHOOSE_TOOL.getWrappedStack());
                }
            }.runTaskLater(CastleRush.getInstance(), 1L);
        }
    }

    @EventHandler
    public void onGameLeave(GameLeaveEvent event){
        WrappedGamePlayer player = event.getPlayer();
        Player wrappedPlayer = player.getPlayer();
        Game game = event.getGame();

        // Check if he left the game during ... or ...
        if(event.getType() == GameLeaveEvent.Type.SERVER_LEAVE
                || event.getType() == GameLeaveEvent.Type.LOBBY_LEAVE){
            int rejoinTime = PluginSettings.REJOIN_TIME;

            if(game.getArena().getGameState() != GameState.INGAME){
                game.broadcast(LanguageManager.get("playerLeftServerDuringLobby")
                        .replace("%player", player.getDisplayName())
                        .replace("%current", game.getArena().getCurrentPlayerSize()-1 + ""));
                GameManager.getLeftForSurePlayer().add(player);
            }
            else{
                game.broadcast(LanguageManager.get("playerLeftServerDuringGame")
                        .replace("%player", player.getDisplayName())
                        .replace("%time", rejoinTime + ""));
                GameManager.addLeft(player);
                return;
            }
        }
        game.leave(player);

        // Check if the time does not run out
        if(event.getType() != GameLeaveEvent.Type.LOBBY_LEAVE){
            if(event.getType() != GameLeaveEvent.Type.REJOIN_TIME_RUNS_OUT){
                player.reset();
            }
            else{
                game.broadcast(LanguageManager.get("kickedBecauseTimeOut").replace("%player",
                        player.getDisplayName()));
            }
        }

        // Get player size
        int currentPlayersSize = game.getArena().getCurrentPlayerSize();

        // Send message only if he left with command
        if(event.getType() == GameLeaveEvent.Type.COMMAND_LEAVE){
            game.broadcast(LanguageManager.get("playerLeftTheGame")
                    .replace("%player", player.getPlayer().getDisplayName())
                    .replace("%current", currentPlayersSize + ""));
        }

        // Check if he left during lobby
        if(game.getArena().getGameState() == GameState.LOBBY){
            // Check if enough players are there
            if(game.getArena().getCurrentPlayerSize() < game.getMinSize()){
                game.broadcast(LanguageManager.get("playersLeftToStart")
                        .replace("%size", (game.getMinSize()-(game.getArena().getCurrentPlayerSize())) + ""));
            }
        }

        // New game master
        if(currentPlayersSize >= 1
                && game.isGamemaster(wrappedPlayer)){
            game.newGameMaster();
        }

        // If is ingame then update scoreboard
        if(game.getArena().getGameState() == GameState.INGAME){
            // Set scoreboard
            SuperLibrary.callEvent(new GameScoreboardUpdateEvent(game, GameScoreboardUpdateEvent.Reason.UPDATE));
        }

        if(currentPlayersSize == 0){
            SuperLibrary.callEvent(new GamePhaseEvent(game, GamePhase.FINISH));
        }
        else if(currentPlayersSize == 1
                && game.getArena().getGameState() == GameState.INGAME){
            game.setWinner(game.getArena().getPlayers().get(0));
            SuperLibrary.callEvent(new GamePhaseEvent(game, GamePhase.END));
        }
    }

    @EventHandler
    public void onGameStateChange(GamePhaseEvent event){
        Game game = event.getGame();
        GamePhase phase = event.getGamePhase();

        game.getArena().setGamePhase(phase);
        new SuperDelayer(0).run(bukkitRunnable -> {
            switch(phase){
                case LOBBY:
                    EventManager.ON_LOBBY.accept(event);
                    break;
                case BUILD:
                    EventManager.ON_BUILD.accept(event);
                    break;
                case CAPTURE:
                    EventManager.ON_CAPTURE.accept(event);
                    break;
                case END:
                    EventManager.ON_END.accept(event);
                    break;
                case FINISH:
                    EventManager.ON_FINISH.accept(event);
                    break;
            }
        });
    }

    @EventHandler
    public void onGameCreate(GameCreateEvent event){
        Player player = event.getPlayer();
        Arena arena = event.getArena();
        GameType type = event.getType();

        ChatManager.info().write(LanguageManager.get("createGameWait"), player);

        if(GameManager.createGame(player, arena, type)){
            // Call event for further things
            SuperLibrary.callEvent(new GameJoinEvent(arena, player, player.getLocation()));
        }
    }

}
