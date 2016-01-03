package de.superioz.cr.common.listener;

import de.superioz.cr.common.stats.StatsManager;
import de.superioz.cr.main.CastleRush;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.UUID;

/**
 * This class was created as a part of CastleRush
 *
 * @author Superioz
 */
public class StatsListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        // Check connection
        if(!CastleRush.getDatabaseManager().check()){
            return;
        }
        // Add to stats
        if(!StatsManager.contains(uuid)){
            StatsManager.add(player);
        }
    }

}
