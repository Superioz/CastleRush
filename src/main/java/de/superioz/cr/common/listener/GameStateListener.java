package de.superioz.cr.common.listener;

import de.superioz.cr.common.WrappedGamePlayer;
import de.superioz.cr.common.events.GameFinishEvent;
import de.superioz.cr.common.events.GameJoinEvent;
import de.superioz.cr.common.events.GameLeaveEvent;
import de.superioz.cr.common.events.GameStartEvent;
import de.superioz.cr.common.game.GameManager;
import de.superioz.cr.common.game.countdowns.BuildCountdown;
import de.superioz.cr.main.CastleRush;
import de.superioz.library.java.util.list.ListUtils;
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
public class GameStateListener implements Listener {

    @EventHandler
    public void onGameJoin(GameJoinEvent event){
        Player player = event.getPlayer();
        GameManager.Game game = event.getGame();

        if(!game.inAnotherWorld(player.getWorld())){
            CastleRush.getChatMessager().send(CastleRush.getProperties().get("arenaMustntInYourWorld"),
                    player.getPlayer());
            return;
        }

        game.join(player.getPlayer(), event.getLoc());
        List<WrappedGamePlayer> currentPlayers = game.getArena().getPlayers();
        List<Location> spawnPoints = game.getArena().getArena().getSpawnPoints();

        // Teleport player to one spawnPoint
        int currentPlayersSize = currentPlayers.size();
        Location spawnPoint = spawnPoints.get(GameManager.getWrappedGamePlayer(player).getGameIndex());

        player.teleport(spawnPoint.clone().add(0, 1, 0));

        // Send message
        game.broadcast(CastleRush.getProperties().get("playerJoinedTheGame")
            .replace("%player", player.getPlayer().getDisplayName()).replace("%current", currentPlayersSize+""));

        // Check players count
        if(currentPlayersSize < 2){
            // There must be more players
            game.broadcast(CastleRush.getProperties().get("playersLeftToStart")
                    .replace("%size", currentPlayersSize + ""));
        }

        String s = ListUtils.insert(GameManager.getWrappedGamePlayer(player).getTeamMatesNames(), ", ");
        CastleRush.getChatMessager().send(CastleRush.getProperties().get("teamMates")
                .replace("%pl", s.isEmpty() ? CastleRush.getProperties().get("youDontHaveTeammates"): s), player);
    }

    @EventHandler
    public void onGameLeave(GameLeaveEvent event){
        WrappedGamePlayer player = event.getPlayer();
        GameManager.Game game = event.getGame();

        // Reset Inventory etc.
        player.clear();
        player.clearInventory();

        game.leave(player);
        player.teleport(player.getJoinLocation());

        // Variables
        List<WrappedGamePlayer> currentPlayers = game.getArena().getPlayers();

        // Teleport player to one spawnPoint
        int currentPlayersSize = currentPlayers.size();

        // Send message
        game.broadcast(CastleRush.getProperties().get("playerLeftTheGame")
            .replace("%player", player.getPlayer().getDisplayName()).replace("%current", currentPlayersSize + ""));

        if(currentPlayersSize == 0){
            game.getArena().setGameState(GameManager.State.LOBBY);
        }
        else if(currentPlayersSize == 1
                && game.getArena().getGameState() != GameManager.State.WAITING){
            game.getArena().setGameState(GameManager.State.LOBBY);

            CastleRush.getPluginManager().callEvent(new GameFinishEvent(game,
                    game.getArena().getPlayers().get(0)));
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
        BuildCountdown.run(game);
    }

    @EventHandler
    public void onGameFinish(GameFinishEvent event){
        WrappedGamePlayer winner = event.getWinner();
        GameManager.Game game = event.getGame();

        // Announcement
        game.broadcast(CastleRush.getProperties().get("playerWonTheGame")
                .replace("%player", winner.getPlayer().getDisplayName()));

        // Set gamestate
        game.getArena().setGameState(GameManager.State.WAITING);

        // Teleport to spawn
        for(WrappedGamePlayer gp : game.getArena().getPlayers()){
            Location spawn = gp.getSpawnLocation();

            gp.clear();
            gp.getPlayer().teleport(spawn.clone().add(0, 1, 0));
        }

        // End of the game
        game.broadcast(CastleRush.getProperties().get("gameEnded"));
    }

}
