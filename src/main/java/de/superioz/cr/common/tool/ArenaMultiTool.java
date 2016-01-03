package de.superioz.cr.common.tool;

import de.superioz.library.minecraft.server.common.item.SimpleItem;
import de.superioz.library.minecraft.server.common.item.SimpleItemTool;
import de.superioz.library.minecraft.server.event.WrappedItemInteractEvent;

import java.util.function.Consumer;

/**
 * This class was created as a part of CastleRush (Spigot)
 *
 * @author Superioz
 */
public class ArenaMultiTool extends SimpleItemTool {

    public ArenaMultiTool(SimpleItem item, Consumer<WrappedItemInteractEvent> consumer){
        super(item, consumer);
    }

}
