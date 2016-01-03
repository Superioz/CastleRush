package de.superioz.cr.common;

import de.superioz.cr.common.arena.ItemKit;
import de.superioz.cr.common.event.GameLeaveEvent;
import de.superioz.cr.common.event.GamePhaseEvent;
import de.superioz.cr.common.game.Game;
import de.superioz.cr.common.game.GameManager;
import de.superioz.cr.common.game.GameState;
import de.superioz.cr.common.game.GameType;
import de.superioz.cr.common.lang.LanguageManager;
import de.superioz.cr.common.settings.PluginSettings;
import de.superioz.cr.common.stats.StatsManager;
import de.superioz.cr.common.timer.CaptureTimer;
import de.superioz.cr.common.tool.GameMasterSettingsTool;
import de.superioz.cr.main.CastleRush;
import de.superioz.cr.util.PluginItems;
import de.superioz.library.main.SuperLibrary;
import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * This class was created as a part of CastleRush
 *
 * @author Superioz
 */
public class EventManager {

    /**
     * What happens when the game comes to the lobby phase
     */
    public static final Consumer<GamePhaseEvent> ON_LOBBY = event -> {
        Game game = event.getGame();
        World world = game.getWorld();

        world.setAutoSave(false);
        world.setTime(0);
        world.setDifficulty(Difficulty.PEACEFUL);

        game.getGameMaster().getPlayer().getInventory().setItem(GameMasterSettingsTool.SLOT,
                PluginItems.GAMEMASTER_SETTINGS_TOOL.getWrappedStack());
    };

    /**
     * What happens when the game comes to the build phase
     */
    public static final Consumer<GamePhaseEvent> ON_BUILD = event -> {
        Game game = event.getGame();
        World world = game.getWorld();

        // Shuffle players
        game.getTeamManager().shuffle(game.getArena().getPlayers());

        world.setTime(0);
        game.getArena().getPlayers().forEach(WrappedGamePlayer::clear);

        // Set walls if gamemode is automatic
        if(game.getType() == GameType.PUBLIC
                && PluginSettings.GAME_WALLS_SET){
            Material mat = PluginSettings.GAME_WALLS_MATERIAL;
            if(mat == null) mat = Material.BEDROCK;
            game.getArena().setWalls(mat);
        }

        // Teleport
        // Now the gamemode is set and the players can start build their castles
        for(WrappedGamePlayer gamePlayer : game.getArena().getPlayers()){
            gamePlayer.getPlayer().teleport(gamePlayer.getPlot().getTeleportPoint());
            gamePlayer.setGameMode(GameMode.CREATIVE);
        }

        game.broadcast(LanguageManager.get("startBuildingCastle"));

        // Start the timer
        game.getBuildCountdown().run();

        // Set gamestate
        game.getArena().setGameState(GameState.INGAME);
    };

    /**
     * What happens when the game comes to the capture phase
     */
    public static final Consumer<GamePhaseEvent> ON_CAPTURE = event -> {
        Game game = event.getGame();

        ItemKit itemKit = game.getArena().getArena().getItemKit();

        // Remove the walls if gamemode is automatic
        if(game.getType() == GameType.PUBLIC){
            new BukkitRunnable() {
                @Override
                public void run(){
                    game.getArena().resetWalls();
                }
            }.runTaskLater(CastleRush.getInstance(), 20L);
        }

        // Players teleport to his spawnpoint
        List<WrappedGamePlayer> gamePlayers = game.getArena().getPlayers().stream().collect(Collectors.toList());
        for(WrappedGamePlayer gamePlayer : gamePlayers){
            // Check if player is there but left the game
            if(gamePlayer.hasLeft()){
                GameManager.removeLeft(gamePlayer);
                GameManager.getLeftForSurePlayer().add(gamePlayer);
                SuperLibrary.callEvent(new GameLeaveEvent(game, gamePlayer,
                        GameLeaveEvent.Type.REJOIN_TIME_RUNS_OUT));
                continue;
            }

            Player p = gamePlayer.getPlayer();

            gamePlayer.clear();
            itemKit.setFor(p);

            gamePlayer.teleport(game.getArena().getArena().getSpawnPoint());
            gamePlayer.resetScoreboard();
            gamePlayer.getPlayer().getInventory().setItem(8, PluginItems.INGAME_TELEPORT_TOOL.getWrappedStack());
        }

        // Start timer
        new BukkitRunnable() {
            @Override
            public void run(){
                game.setTimeStamp(System.currentTimeMillis());
                new CaptureTimer().run(game);
            }
        }.runTaskLater(CastleRush.getInstance(), 40L);
    };

    /**
     * What happens when the game comes to the end phase
     */
    public static final Consumer<GamePhaseEvent> ON_END = event -> {
        Game game = event.getGame();

        // Broadcast who won the game
        if(game.getWinner() != null){
            game.broadcast(LanguageManager.get("playerWonTheGame")
                    .replace("%player", game.getWinner().getPlayer().getDisplayName()));
        }

        // Set gamestate
        game.getArena().setGameState(GameState.WAITING);
        game.getArena().resetWalls();

        // Teleport to spawn
        List<WrappedGamePlayer> gamePlayers = game.getArena().getPlayers().stream().collect(Collectors.toList());
        for(WrappedGamePlayer gp : gamePlayers){
            if(gp.hasLeft()){
                game.leave(gp);
                continue;
            }

            gp.resetScoreboard();
            gp.clear();
            gp.getPlayer().teleport(gp.getSpawnLocation());
        }

        // End of the game
        if(game.getType() == GameType.PRIVATE){
            game.broadcast(LanguageManager.get("gameEnded"));
        }
        else{
            game.getGameCountdown().runEndTimer();
        }

        // Give stats to players
        StatsManager.updateStatistics(game);
    };

    /**
     * What happens when the game comes to the finish phase
     */
    public static final Consumer<GamePhaseEvent> ON_FINISH = event -> {
        Game game = event.getGame();

        // Set walls
        game.getArena().resetWalls();

        for(WrappedGamePlayer p : game.getArena().getPlayers()){
            if(p == null
                    || p.hasLeft())
                continue;

            p.reset();
        }
        game.leaveAll();
        GameState state = game.getArena().getGameState();

        // Set gamestate
        game.getArena().setGameState(GameState.LOBBY);

        GameManager.removeGameFromQueue(game, state != GameState.LOBBY);
    };

}
