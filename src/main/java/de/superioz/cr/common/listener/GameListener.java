package de.superioz.cr.common.listener;

import de.superioz.cr.common.WrappedGamePlayer;
import de.superioz.cr.common.events.GameFinishEvent;
import de.superioz.cr.common.events.GameJoinEvent;
import de.superioz.cr.common.events.GameLeaveEvent;
import de.superioz.cr.common.events.GameStartEvent;
import de.superioz.cr.common.game.GameManager;
import de.superioz.library.minecraft.server.util.task.Countdown;
import org.bukkit.Bukkit;
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

        game.join(player);
        List<WrappedGamePlayer> currentPlayers = game.getArena().getPlayers();
        List<Location> spawnPoints = game.getArena().getArena().getSpawnPoints();

        // Teleport player to one spawnPoint
        int currentPlayersSize = currentPlayers.size();
        Location spawnPoint = spawnPoints.get(currentPlayersSize-1);
        player.teleport(spawnPoint.clone().add(0, 1, 0));

        // Send message
        game.broadcast("&b" + player.getDisplayName() + " &7joined the game. " +
                "[&b" + currentPlayersSize + "&7/" + game.getArena().getMaxPlayers() + "]");

        // Check players count
        if(currentPlayersSize != game.getArena().getMaxPlayers()){
            // There must be more players
            game.broadcast("&c" + (game.getArena().getMaxPlayers()-currentPlayersSize) + " player(s) left to start!");
            return;
        }

        // If count is correct, then wait for game start
        game.getArena().setGameState(GameManager.State.FULL);
        game.broadcast("&7There are enough player(s) to start. You can now use &b/cr startgame");
    }

    @EventHandler
    public void onGameLeave(GameLeaveEvent event){
        Player player = event.getPlayer();
        GameManager.Game game = event.getGame();

        // Reset Inventory etc.
        game.leave(player);
        game.clear(player);
        player.teleport(Bukkit.getWorlds().get(0).getSpawnLocation().add(0, 1, 0));

        // Variables
        List<WrappedGamePlayer> currentPlayers = game.getArena().getPlayers();

        // Teleport player to one spawnPoint
        int currentPlayersSize = currentPlayers.size();

        // Send message
        game.broadcast("&c" + player.getDisplayName() + " &7left the game. " +
                "[&c" + currentPlayersSize + "&7/" + game.getArena().getMaxPlayers() + "]");

        // Check players count
        if(currentPlayersSize != game.getArena().getMaxPlayers()){
            // There must be more players
            game.broadcast("&c" + (game.getArena().getMaxPlayers()-currentPlayersSize) + " player(s) left to start!");
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
        game.broadcast("&7You can now start to build your &bcastle&7!");

        // Start the timer
        countdown = new Countdown(3 * 60);
        countdown.run(endRunnable -> {
            // What happens at the end
            // Timer runs out - gamestate dont change
            // now the players plays another castle and they have to try to capture the wool
            game.prepareNextState();
            game.broadcast("&7The next state began! Try to &bcapture your enemy's castle&7!");
        }, startRunnable -> {
            int counter = countdown.getCounter();

            if(counter % 1800 == 0){
                game.broadcast("&7There are &b"+(counter/60)+" &7minute(s) left!");
            }
        });
    }

    @EventHandler
    public void onGameFinish(GameFinishEvent event){
        Player winner = event.getWinner();
        GameManager.Game game = event.getGame();

        // Announcement
        game.broadcast("&7The player &b"+winner.getDisplayName().toUpperCase()+ " &7won the game!");

        // Set gamestate
        game.getArena().setGameState(GameManager.State.WAITING);

        // Teleport to spawn
        for(WrappedGamePlayer gp : game.getArena().getPlayers()){
            int index = gp.getGameIndex();
            Location spawn = game.getArena().getArena().getSpawnPoints().get(index);

            game.clear(gp.getPlayer());
            gp.getPlayer().teleport(spawn.clone().add(0, 1, 0));
        }

        // End of the game
        game.broadcast("&7The game ended. You can now use &b/cr finishgame");
    }

}
