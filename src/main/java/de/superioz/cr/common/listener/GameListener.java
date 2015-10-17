package de.superioz.cr.common.listener;

import de.superioz.cr.common.WrappedGamePlayer;
import de.superioz.cr.common.events.GameJoinEvent;
import de.superioz.cr.common.events.GameLeaveEvent;
import de.superioz.cr.common.game.GameManager;
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
        player.teleport(spawnPoint.add(0, 0.25D, 0));

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
        List<Location> spawnPoints = game.getArena().getArena().getSpawnPoints();

        // Teleport player to one spawnPoint
        int currentPlayersSize = currentPlayers.size();

        // Send message
        game.broadcast("&c" + player.getDisplayName() + " &7left the game. " +
                "[&c" + currentPlayersSize + "&7/" + game.getArena().getMaxPlayers() + "]");

        // Check players count
        if(currentPlayersSize != game.getArena().getMaxPlayers()){
            // There must be more players
            game.broadcast("&c" + (game.getArena().getMaxPlayers()-currentPlayersSize) + " player(s) left to start!");
            return;
        }
    }

}
