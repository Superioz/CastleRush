package de.superioz.cr.common.listener.game;

import de.superioz.cr.common.WrappedGamePlayer;
import de.superioz.cr.common.event.GamePhaseEvent;
import de.superioz.cr.common.game.*;
import de.superioz.cr.common.settings.PluginSettings;
import de.superioz.library.main.SuperLibrary;
import de.superioz.library.minecraft.server.util.BukkitUtilities;
import de.superioz.library.minecraft.server.util.LocationUtil;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.List;
import java.util.stream.Collectors;

/**
 * This class was created as a part of CastleRush (Spigot)
 *
 * @author Superioz
 */
public class GamePlotListener implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEvent event){
        Player player = event.getPlayer();

        if(!GameManager.isIngame(player.getUniqueId()))
            return;
        WrappedGamePlayer gp = GameManager.getWrappedGamePlayer(player);
        assert gp != null;

        if(event.getItem() == null)
            return;

        if(event.getItem().getType() == Material.WATER_BUCKET
                || event.getItem().getType() == Material.LAVA_BUCKET){
            Block block = event.getClickedBlock();

            if(block == null)
                return;

            GamePlot plot = gp.getPlot();
            event.setCancelled(!plot.isPart(block.getLocation()));
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event){
        Player player = event.getPlayer();

        if(!GameManager.isIngame(player.getUniqueId())){
            return;
        }
        Game game = GameManager.getGame(player);
        Block block = event.getBlock();
        Location loc = LocationUtil.fix(block.getLocation());
        WrappedGamePlayer gamePlayer = GameManager.getWrappedGamePlayer(player);

        assert game != null;
        if(game.getArena().getGameState() != GameState.INGAME){
            event.setCancelled(true);
            return;
        }

        // Check if block is from his plot
        assert gamePlayer != null;
        GamePlot plot = gamePlayer.getPlot();
        boolean flag = plot.isPart(loc);

        if(PluginSettings.ALLOWED_BLOCKS.contains(block.getType().name().toLowerCase())
                && player.getGameMode() != GameMode.CREATIVE){
            return;
        }

        if(!flag && player.getGameMode() == GameMode.SURVIVAL
                && block.getType() == Material.WOOL){
            // Player wons the game
            game.setWinner(gamePlayer);
            SuperLibrary.callEvent(new GamePhaseEvent(game, GamePhase.END));

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

        if(!GameManager.isIngame(player.getUniqueId())){
            return;
        }
        Game game = GameManager.getGame(player);
        Block block = event.getBlock();
        Location loc = LocationUtil.fix(block.getLocation());
        WrappedGamePlayer gamePlayer = GameManager.getWrappedGamePlayer(player);

        assert game != null;
        if(game.getArena().getGameState() != GameState.INGAME){
            event.setCancelled(true);
        }

        // Check if block is from his plot
        assert gamePlayer != null;
        GamePlot plot = gamePlayer.getPlot();
        boolean flag = plot.isPart(loc);

        if(PluginSettings.ALLOWED_BLOCKS.contains(block.getType().name().toLowerCase())){
            return;
        }

        if(flag){
            return;
        }
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlotEnter(PlayerMoveEvent event){
        Player player = event.getPlayer();

        if(!GameManager.isIngame(player.getUniqueId())){
            return;
        }
        if((event.getFrom().getBlockX() == event.getTo().getBlockX()
                && event.getFrom().getBlockZ() == event.getTo().getBlockZ())){
            return;
        }

        Game game = GameManager.getGame(player); assert game != null;
        WrappedGamePlayer gamePlayer = GameManager.getWrappedGamePlayer(player);
        GamePlot plot = gamePlayer.getPlot();
        Location loc = event.getTo().getBlock().getLocation();
        GamePlot newPlot = null;

        // Check null
        if(plot == null || loc == null){
            return;
        }

        // Check
        if(plot.isPart(LocationUtil.fix(event.getFrom().getBlock().getLocation()))){
            return;
        }
        else{
            newPlot = game.getArena().getArena().getPlot(loc);

            if(newPlot == null)
                return;
        }

        // Check
        if(player.getGameMode() == GameMode.CREATIVE
                && game.getArena().getGamePhase() == GamePhase.CAPTURE
                && !gamePlayer.getPlot().equals(newPlot)){
            player.setGameMode(GameMode.SURVIVAL);
        }

        // Check if he can enter this plot
        if(game.getArena().getGamePhase() == GamePhase.BUILD
                && !plot.equals(newPlot)
                && !game.getSettings().canEnterPlotDuringBuild()){
            BukkitUtilities.pushAwayEntity(player, loc, 5d);
        }
    }

    @EventHandler
    public void onPlotLeave(PlayerMoveEvent event){
        Player player = event.getPlayer();

        if(!GameManager.isIngame(player.getUniqueId())){
            return;
        }
        if((event.getFrom().getBlockX() == event.getTo().getBlockX()
                && event.getFrom().getBlockZ() == event.getTo().getBlockZ())){
            return;
        }

        Game game = GameManager.getGame(player);
        WrappedGamePlayer gamePlayer = GameManager.getWrappedGamePlayer(player);

        // Check if phase is already capture
        assert game != null;
        if(game.getArena().getGamePhase() != GamePhase.CAPTURE){
            return;
        }

        GamePlot plot = gamePlayer.getPlot();
        Location loc = LocationUtil.fix(event.getFrom().getBlock().getLocation());

        if(!plot.isPart(loc)
                || plot.isPart(LocationUtil.fix(event.getTo().getBlock().getLocation()))){
            return;
        }

        if(player.getGameMode() == GameMode.CREATIVE){
            player.setGameMode(GameMode.SURVIVAL);
        }
    }

    @EventHandler
    public void onLiquidFlow(BlockFromToEvent event){
        Block to = event.getToBlock();
        Block from = event.getBlock();

        if(GameManager.isPlotPart(from.getLocation())
                && !GameManager.isPlotPart(to.getLocation())){
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event){
        List<Block> eventBlockList = event.blockList().stream().collect(Collectors.toList());
        event.blockList().clear();

        eventBlockList.stream().filter(GameManager::hasPlot).forEach(b -> b.setType(Material.AIR));
    }


}
