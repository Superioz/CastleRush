package de.superioz.cr.common.tool;

import de.superioz.cr.common.WrappedGamePlayer;
import de.superioz.cr.common.game.GameManager;
import de.superioz.cr.util.PluginItems;
import de.superioz.library.minecraft.server.common.inventory.SuperInventory;
import de.superioz.library.minecraft.server.common.item.SimpleItemTool;
import org.bukkit.entity.Player;

/**
 * This class was created as a part of CastleRush
 *
 * @author Superioz
 */
public class TeamChooseTool extends SimpleItemTool {

    public TeamChooseTool(){
        super(PluginItems.TEAM_CHOOSE_TOOL, event -> {
            Player player = event.getPlayer();

            if(!GameManager.isIngame(player.getUniqueId())){
                return;
            }

            WrappedGamePlayer gamePlayer = GameManager.getWrappedGamePlayer(player);
            SuperInventory superInventory = gamePlayer.getGame().getTeamManager().getInventory();

            player.openInventory(superInventory.build());
        });
        super.setStaticPlace(true);
    }
}
