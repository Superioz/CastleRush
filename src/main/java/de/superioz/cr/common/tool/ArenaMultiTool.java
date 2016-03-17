package de.superioz.cr.common.tool;


import de.superioz.library.bukkit.common.item.SimpleItem;
import de.superioz.library.bukkit.common.item.SimpleItemTool;
import de.superioz.library.bukkit.event.WrappedItemInteractEvent;

import java.util.function.Consumer;

/**
 * This class was created as a part of CastleRush (Spigot)
 *
 * @author Superioz
 */
public class ArenaMultiTool extends SimpleItemTool {

    public ArenaMultiTool(SimpleItem item, de.superioz.library.java.util.Consumer<WrappedItemInteractEvent> consumer){
        super(item, consumer);
    }

}
