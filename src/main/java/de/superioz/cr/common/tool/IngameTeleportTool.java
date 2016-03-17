package de.superioz.cr.common.tool;

import de.superioz.cr.common.WrappedGamePlayer;
import de.superioz.cr.common.game.GameManager;
import de.superioz.cr.common.inv.GameTeleportInventory;
import de.superioz.cr.util.PluginItems;
import de.superioz.library.bukkit.common.item.SimpleItemTool;
import org.bukkit.entity.Player;

/**
 * This class was created as a part of CastleRush
 *
 * @author Superioz
 */
public class IngameTeleportTool extends SimpleItemTool {

    public static final int SLOT = 8;

    public IngameTeleportTool(){
        super(PluginItems.INGAME_TELEPORT_TOOL, event -> {
            Player player = event.getPlayer();

            if(!GameManager.isIngame(player.getUniqueId())){
                return;
            }
            WrappedGamePlayer gamePlayer = GameManager.getWrappedGamePlayer(player);
            GameTeleportInventory.open(gamePlayer);
        });
        super.setStaticPlace(true);
    }

}
