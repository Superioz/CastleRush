package de.superioz.cr.common.tool;

import de.superioz.cr.common.ChatManager;
import de.superioz.cr.util.PluginItems;
import de.superioz.library.bukkit.common.item.SimpleItemTool;

/**
 * This class was created as a part of CastleRush
 *
 * @author Superioz
 */
public class GameMasterSettingsTool extends SimpleItemTool {

    public static final int SLOT = 8;

    public GameMasterSettingsTool(){
        super(PluginItems.GAMEMASTER_SETTINGS_TOOL, event -> {
            event.getEvent().setCancelled(true);
            ChatManager.info().write("&cComing soon! :)", event.getPlayer());
        });
        super.setStaticPlace(true);
    }

}
