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
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

/**
 * This class was created as a part of CastleRush (Spigot)
 *
 * @author Superioz
 */
public class GameBuildListener implements Listener {

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


}
