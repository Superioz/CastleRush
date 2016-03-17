package de.superioz.cr.common.inv;

import de.superioz.library.bukkit.common.inventory.PageableInventory;

/**
 * This class was created as a part of CastleRush
 *
 * @author Superioz
 */
public abstract class StaticPageableInventory {

    private static PageableInventory inventory;

    protected static PageableInventory getInventory(){
        return inventory;
    }

    protected static boolean getFlag(){
        return inventory != null;
    }

    protected static void setInventory(PageableInventory inv){
        inventory = inv;
    }

}
