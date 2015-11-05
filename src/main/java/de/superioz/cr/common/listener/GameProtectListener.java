package de.superioz.cr.common.listener;

import de.superioz.cr.common.WrappedGamePlayer;
import de.superioz.cr.common.events.GameFinishEvent;
import de.superioz.cr.common.events.GameLeaveEvent;
import de.superioz.cr.common.game.GameManager;
import de.superioz.cr.common.game.GamePlot;
import de.superioz.cr.main.CastleRush;
import de.superioz.library.minecraft.server.util.LocationUtils;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * This class was created as a part of CastleRush (Spigot)
 *
 * @author Superioz
 */
public class GameProtectListener implements Listener {

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event){
        Player player = event.getPlayer();

        if(!GameManager.isIngame(player)){
            return;
        }
        GameManager.Game game = GameManager.getGame(player);
        Block block = event.getBlock();
        Location loc = LocationUtils.fix(block.getLocation());
        WrappedGamePlayer gamePlayer = GameManager.getWrappedGamePlayer(player);

        assert game != null;
        if(game.getArena().getGameState() != GameManager.State.INGAME){
            event.setCancelled(true);
            return;
        }

        // Check if block is from his plot
        assert gamePlayer != null;
        GamePlot plot = gamePlayer.getPlot();
        boolean flag = plot.isPart(loc);

        if(GameManager.allowedBlocks.contains(block.getType()) && player.getGameMode() != GameMode.CREATIVE){
            return;
        }

        if(!flag && player.getGameMode() == GameMode.SURVIVAL
                && block.getType() == Material.WOOL){
            // Player wons the game
            CastleRush.getPluginManager().callEvent(new GameFinishEvent(game, gamePlayer));
            event.setCancelled(true);
            block.setType(Material.AIR);
            return;
        }

        if(flag){
            return;
        }
        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event){
        Player player = event.getPlayer();


        if(!GameManager.isIngame(player)){
            return;
        }
        GameManager.Game game = GameManager.getGame(player);
        Block block = event.getBlock();
        Location loc = LocationUtils.fix(block.getLocation());
        WrappedGamePlayer gamePlayer = GameManager.getWrappedGamePlayer(player);

        assert game != null;
        if(game.getArena().getGameState() != GameManager.State.INGAME){
            event.setCancelled(true);
        }

        // Check if block is from his plot
        assert gamePlayer != null;
        GamePlot plot = gamePlayer.getPlot();
        boolean flag = plot.isPart(loc);

        if(GameManager.allowedBlocks.contains(block.getType())){
            return;
        }

        if(flag){
            return;
        }
        event.setCancelled(true);
    }

    @EventHandler
    public void onHit(EntityDamageByEntityEvent event){
        if(event.getEntity() instanceof Player){
            Player p1 = (Player) event.getEntity();

            if(event.getDamager() instanceof Player){
                Player p2 = (Player) event.getDamager();

                if(GameManager.isIngame(p1)
                        && GameManager.isIngame(p2)){
                    WrappedGamePlayer gp1 = GameManager.getWrappedGamePlayer(p1);
                    WrappedGamePlayer gp2 = GameManager.getWrappedGamePlayer(p2);

                    if(gp2.getTeamMates().contains(gp1))
                        event.setCancelled(true);
                }
            }
            else if(event.getDamager() instanceof Projectile){
                ProjectileSource source = ((Projectile)event.getDamager()).getShooter();

                if(source instanceof Player){
                    Player p2 = (Player) source;

                    if(GameManager.isIngame(p1)
                            && GameManager.isIngame(p2)){
                        WrappedGamePlayer gp1 = GameManager.getWrappedGamePlayer(p1);
                        WrappedGamePlayer gp2 = GameManager.getWrappedGamePlayer(p2);

                        if(gp2.getTeamMates().contains(gp1))
                            event.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onDie(PlayerDeathEvent event){
        Player player = event.getEntity();

        if(!GameManager.isIngame(player))
            return;

        event.getDrops().clear();
        event.setDroppedExp(0);
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event){
        Player player = event.getPlayer();

        if(!GameManager.isIngame(player))
            return;
        WrappedGamePlayer gp = GameManager.getWrappedGamePlayer(player); assert gp != null;

        event.setRespawnLocation(gp.getSpawnLocation());

        new BukkitRunnable(){
            @Override
            public void run(){
                gp.getGame().getArena().getArena().getItemKit().resetArmor(gp.getPlayer());
            }
        }.runTaskLater(CastleRush.getInstance(), 1L);
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event){
        Player player = event.getPlayer();

        if(!GameManager.isIngame(player))
            return;
        WrappedGamePlayer gp = GameManager.getWrappedGamePlayer(player); assert gp != null;
        GameManager.Game game = gp.getGame();

        if((game.getArena().getGameState() != GameManager.State.INGAME)
                || GameListener.countdown == null
                || GameListener.countdown.counter <= 0){
            CastleRush.getPluginManager().callEvent(new GameLeaveEvent(game, gp));
        }
    }


}
