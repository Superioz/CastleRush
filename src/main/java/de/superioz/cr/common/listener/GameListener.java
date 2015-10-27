package de.superioz.cr.common.listener;

import de.superioz.cr.common.WrappedGamePlayer;
import de.superioz.cr.common.events.*;
import de.superioz.cr.common.game.GameManager;
import de.superioz.cr.main.CastleRush;
import de.superioz.library.minecraft.server.util.task.Countdown;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;

/**
 * This class was created as a part of CastleRush (Spigot)
 *
 * @author Superioz
 */
public class GameListener implements Listener {

    public static Countdown countdown;

    @EventHandler
    public void onGameJoin(GameJoinEvent event){
        Player player = event.getPlayer();
        GameManager.Game game = event.getGame();

        if(!game.inAnotherWorld(player.getWorld())){
            CastleRush.getChatMessager().send(CastleRush.getProperties().get("arenaMustntInYourWorld"), player);
            return;
        }

        game.join(player, event.getLoc());
        List<WrappedGamePlayer> currentPlayers = game.getArena().getPlayers();
        List<Location> spawnPoints = game.getArena().getArena().getSpawnPoints();

        // Teleport player to one spawnPoint
        int currentPlayersSize = currentPlayers.size();
        Location spawnPoint = spawnPoints.get(currentPlayersSize-1);
        player.teleport(spawnPoint.clone().add(0, 1, 0));

        // Send message
        game.broadcast(CastleRush.getProperties().get("playerJoinedTheGame")
            .replace("%player", player.getDisplayName()).replace("%current", currentPlayersSize+"")
            .replace("%max", game.getArena().getMaxPlayers()+""));

        // Check players count
        if(currentPlayersSize < 2){
            // There must be more players
            game.broadcast(CastleRush.getProperties().get("playersLeftToStart")
                    .replace("%size", currentPlayersSize + ""));
            return;
        }

        // If count is correct, then wait for game start
        game.getArena().setGameState(GameManager.State.FULL);
        game.broadcast(CastleRush.getProperties().get("useCommandForStartGame"));
        game.broadcast(CastleRush.getProperties().get("useCommandForWalls"));
    }

    @EventHandler
    public void onGameLeave(GameLeaveEvent event){
        Player player = event.getPlayer();
        GameManager.Game game = event.getGame();

        // Reset Inventory etc.
        WrappedGamePlayer gp = GameManager.getWrappedGamePlayer(player); assert gp != null;
        gp.clear();

        game.leave(player);
        gp.teleport(gp.getJoinLocation());

        // Variables
        List<WrappedGamePlayer> currentPlayers = game.getArena().getPlayers();

        // Teleport player to one spawnPoint
        int currentPlayersSize = currentPlayers.size();

        // Send message
        game.broadcast(CastleRush.getProperties().get("playerLeftTheGame")
            .replace("%player", player.getDisplayName()).replace("%current", currentPlayersSize+"")
            .replace("%max", game.getArena().getMaxPlayers()+""));

        if(currentPlayersSize == 0){
            game.getArena().setGameState(GameManager.State.LOBBY);
        }
        else if(currentPlayersSize == 1){
            game.getArena().setGameState(GameManager.State.LOBBY);

            CastleRush.getPluginManager().callEvent(new GameFinishEvent(game,
                    game.getArena().getPlayers().get(0).getPlayer()));
        }
    }

    @EventHandler
    public void onGameStart(GameStartEvent event){
        GameManager.Game game = event.getGame();
        game.getArena().setGameState(GameManager.State.INGAME);
        game.prepareGame();

        // Now the gamemode is set and the players can start build their castles
        for(WrappedGamePlayer gamePlayer : game.getArena().getPlayers())
            gamePlayer.getPlayer().teleport(gamePlayer.getPlot().getTeleportPoint());
        game.broadcast(CastleRush.getProperties().get("startBuildingCastle"));

        // Start the timer
        countdown = new Countdown(2*60);
        countdown.run(endRunnable -> {
            // What happens at the end
            // Timer runs out - gamestate dont change
            // now the players plays another castle and they have to try to capture the wool

            if(game.getArena().getPlayers().size() < 2){
                CastleRush.getPluginManager().callEvent(new GameFinishEvent(game,
                        game.getArena().getPlayers().get(0).getPlayer()));
                return;
            }

            game.prepareNextState();
            game.broadcast(CastleRush.getProperties().get("startCaptureCastle"));
        }, startRunnable -> {
            int counter = countdown.getCounter();

            if(counter % (60*5) == 0){
                game.broadcast(CastleRush.getProperties().get("thereAreMinutesLeft")
                        .replace("%time", (counter / 60) + ""));
            }
            else if(counter <= 10){
                game.broadcast(CastleRush.getProperties().get("thereAreSecondsLeft")
                        .replace("%seconds", counter + ""));
            }
        });
    }

    @EventHandler
    public void onGameFinish(GameFinishEvent event){
        Player winner = event.getWinner();
        GameManager.Game game = event.getGame();

        // Announcement
        game.broadcast(CastleRush.getProperties().get("playerWonTheGame")
                .replace("%player", winner.getDisplayName()));

        // Set gamestate
        game.getArena().setGameState(GameManager.State.WAITING);

        // Teleport to spawn
        for(WrappedGamePlayer gp : game.getArena().getPlayers()){
            int index = gp.getGameIndex();
            Location spawn = game.getArena().getArena().getSpawnPoints().get(index);

            gp.clear();
            gp.getPlayer().teleport(spawn.clone().add(0, 1, 0));
        }

        // End of the game
        game.broadcast(CastleRush.getProperties().get("gameEnded"));
    }

}
