package de.superioz.cr.common.listener;

import de.superioz.cr.common.WrappedGamePlayer;
import de.superioz.cr.common.events.GameFinishEvent;
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
import org.bukkit.event.player.PlayerRespawnEvent;
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

        if(GameManager.allowedBlocks.contains(block.getType())){
            return;
        }

        if(!flag && player.getGameMode() == GameMode.SURVIVAL
                && block.getType() == Material.WOOL){
            // Player wons the game
            CastleRush.getPluginManager().callEvent(new GameFinishEvent(game, player));
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
        if(!(event.getEntity() instanceof Player) || (event.getDamager() instanceof Player)){
            return;
        }
        else if(event.getDamager() instanceof Projectile){
            return;
        }

        Player player = (Player) event.getEntity();

        if(GameManager.isIngame(player)){
            event.setCancelled(true);
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
                gp.getGame().getArena().getArena().getItemKit().setFor(gp.getPlayer());
            }
        }.runTaskLater(CastleRush.getInstance(), 1L);
    }


}
