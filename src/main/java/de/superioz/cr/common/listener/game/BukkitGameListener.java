package de.superioz.cr.common.listener.game;

import de.superioz.cr.common.lang.LanguageManager;
import de.superioz.cr.common.WrappedGamePlayer;
import de.superioz.cr.common.arena.ItemKit;
import de.superioz.cr.common.event.GameLeaveEvent;
import de.superioz.cr.common.event.GameScoreboardUpdateEvent;
import de.superioz.cr.common.game.Game;
import de.superioz.cr.common.game.GameManager;
import de.superioz.cr.common.game.GamePhase;
import de.superioz.cr.common.game.GameState;
import de.superioz.cr.common.settings.PluginSettings;
import de.superioz.cr.main.CastleRush;
import de.superioz.cr.util.InventoryBackup;
import de.superioz.library.bukkit.BukkitLibrary;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;

/**
 * This class was created as a part of CastleRush
 *
 * @author Superioz
 */
public class BukkitGameListener implements Listener {

    public HashMap<Player, ItemStack[]> deathDrops = new HashMap<>();

    @EventHandler
    public void onDeath(PlayerDeathEvent event){
        Player player = event.getEntity();

        if(!GameManager.isIngame(player.getUniqueId()))
            return;
        WrappedGamePlayer gp = GameManager.getWrappedGamePlayer(player);

        // Clear drops
        event.setDroppedExp(0);
        event.getDrops().clear();

        InventoryBackup.save(player);
        gp.clearInventory();
    }

    @EventHandler
    public void onHit(EntityDamageByEntityEvent event){
        Entity e = event.getEntity();
        Player damager = null;

        // Check damager and init player object
        if(event.getDamager() instanceof Player){
            damager = (Player) event.getDamager();
        }
        else if(event.getDamager() instanceof Projectile
                && (((Projectile) event.getDamager()).getShooter() instanceof Player)){
            damager = (Player)((Projectile) event.getDamager()).getShooter();
        }

        // Check if damager equals null
        if(damager == null)
            return;

        if(!GameManager.isIngame(damager.getUniqueId())){
            return;
        }

        // If damaged is a player
        if(event.getEntity() instanceof Player){
            Player p1 = (Player) e;

            if(GameManager.isIngame(p1.getUniqueId())){
                WrappedGamePlayer gp1 = GameManager.getWrappedGamePlayer(p1);
                WrappedGamePlayer gp2 = GameManager.getWrappedGamePlayer(damager);

                if(gp2.getTeamMates().contains(gp1)
                        || gp1.getGame().getArena().getGameState() != GameState.INGAME
                        || gp2.getPlayer().getGameMode() == GameMode.CREATIVE)
                    event.setCancelled(true);
            }
        }
        else if(event.getEntity() instanceof LivingEntity){
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event){
        Player player = event.getPlayer();

        if(!GameManager.isIngame(player.getUniqueId()))
            return;
        WrappedGamePlayer gp = GameManager.getWrappedGamePlayer(player);
        Game game = gp.getGame();

        // Set respawn
        event.setRespawnLocation(gp.getGame().getArena().getArena().getSpawnPoint());

        // Restore inventory
        InventoryBackup.restore(gp.getPlayer());

        // Give game master tool if he is the gamemaster
        if(game.getArena().getGameState() == GameState.LOBBY){
            if(game.isGamemaster(gp.getPlayer()))
                game.giveGameMasterTool();
        }

        // Give kit to players
        if(game.getArena().getGamePhase() == GamePhase.CAPTURE){
            new BukkitRunnable() {
                @Override
                public void run(){
                    // Set armor
                    ItemKit kit = gp.getGame().getArena().getArena().getItemKit();
                    kit.setSoftFor(gp.getPlayer());
                }
            }.runTaskLater(CastleRush.getInstance(), 1L);
        }
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event){
        Player player = event.getPlayer();

        if(!GameManager.isIngame(player.getUniqueId()))
            return;
        WrappedGamePlayer gp = GameManager.getWrappedGamePlayer(player);
        Game game = gp.getGame();

        BukkitLibrary.callEvent(new GameLeaveEvent(game, gp, GameLeaveEvent.Type.SERVER_LEAVE));
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();
        WrappedGamePlayer gamePlayer = GameManager.getLeft(player);

        if(GameManager.containsForSure(player)){
            WrappedGamePlayer gamePlayer1 = GameManager.getForSure(player);
            assert gamePlayer1 != null;

            // Reset
            gamePlayer1.reset();
            GameManager.getLeftForSurePlayer().remove(gamePlayer1);
            return;
        }

        if(gamePlayer != null){
            if(gamePlayer.rejoin()
                    && gamePlayer.getGame() != null
                    && gamePlayer.getGame().isRunning()
                    && gamePlayer.getGame().getArena().getGameState() == GameState.INGAME){
                gamePlayer.getGame().broadcast(LanguageManager.get("rejoinedInTime")
                        .replace("%player", player.getDisplayName()));

                if(gamePlayer.getGame().getArena().getGamePhase() == GamePhase.BUILD){
                    new BukkitRunnable() {
                        @Override
                        public void run(){
                            gamePlayer.setGameMode(GameMode.CREATIVE);
                        }
                    }.runTaskLater(CastleRush.getInstance(), 20L);
                }

                // Set scoreboard
                BukkitLibrary.callEvent(new GameScoreboardUpdateEvent(gamePlayer.getGame(), GameScoreboardUpdateEvent.Reason.UPDATE));
            }
            else{
                if(gamePlayer.getGame() != null
                        && gamePlayer.getGame().getArena().getGameState() == GameState.INGAME){
                    gamePlayer.getGame().broadcast(LanguageManager.get("doesntRejoinedInTime")
                            .replace("%player", player.getDisplayName()));
                    BukkitLibrary.callEvent(new GameLeaveEvent(gamePlayer.getGame(), gamePlayer, GameLeaveEvent.Type.COMMAND_LEAVE));
                }
                else{
                    // Reset
                    gamePlayer.reset();
                }
            }
            GameManager.removeLeft(gamePlayer);
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event){
        Player player = event.getPlayer();

        if(!GameManager.isIngame(player.getUniqueId()))
            return;
        WrappedGamePlayer gp = GameManager.getWrappedGamePlayer(player);

        if(event.getItem() == null)
            return;

        if(gp.getPlayer().getGameMode() == GameMode.SURVIVAL
                && (event.getItem().getType() == Material.WATER_BUCKET
                || event.getItem().getType() == Material.LAVA_BUCKET)){
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event){
        Player player = event.getPlayer();

        if(!GameManager.isIngame(player.getUniqueId()))
            return;
        WrappedGamePlayer gp = GameManager.getWrappedGamePlayer(player);
        Game game = gp.getGame();

        if(game.getArena().getGamePhase() != GamePhase.LOBBY)
            return;

        Location to = event.getTo();
        Location spawn = gp.getSpawnLocation();

        int distance = PluginSettings.LOBBY_MAX_DISTANCE;
        if((distance != -1)
                && to.distance(spawn) > distance){
            gp.teleport(spawn);
        }
    }

}
